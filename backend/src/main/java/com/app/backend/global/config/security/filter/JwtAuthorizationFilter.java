package com.app.backend.global.config.security.filter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.app.backend.domain.user.entity.Role;
import com.app.backend.domain.user.entity.User;
import com.app.backend.global.config.security.config.JwtConfig;
import com.app.backend.global.config.security.constant.AuthConstant;
import com.app.backend.global.config.security.info.CustomUserDetails;
import com.app.backend.global.config.security.util.AuthResponse;
import com.app.backend.global.config.security.util.CookieProvider;
import com.app.backend.global.config.security.util.JwtProvider;
import com.app.backend.global.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

	private final JwtConfig jwtConfig;
	private final JwtProvider jwtProvider;
	private final ObjectMapper objectMapper;
	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getRequestURI();
		return path.startsWith("/oauth2/") || path.startsWith("/login/oauth2/");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		accessFilter(request, response, filterChain);
	}

	private void accessFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		String authorization = request.getHeader(AuthConstant.AUTHENTICATION);

		if (authorization == null) {
			filterChain.doFilter(request, response);
			return;
		}

		String accessToken = authorization.substring(7);

		if (redisTemplate.opsForValue().get(accessToken) != null) {
			log.debug("로그아웃 처리 된 AccessToken {}", accessToken);
			AuthResponse.failLogin(
				response,
				ApiResponse.of(false, HttpStatus.BAD_REQUEST, "로그아웃 처리된 AccessToken 입니다"),
				HttpStatus.BAD_REQUEST.value(),
				objectMapper);
			return;
		}

		try {
			jwtProvider.isExpired(accessToken);
		} catch (ExpiredJwtException e) {
			log.debug("기간 만료된 AccessToken {}", accessToken);
			reissueFilter(request, response, filterChain);
			return;
		} catch (JwtException e) {
			log.debug("지원하지 않는 AccessToken {}", accessToken);
			AuthResponse.failLogin(
				response,
				ApiResponse.of(false, HttpStatus.BAD_REQUEST, "AccessToken 검증 실패"),
				HttpStatus.BAD_REQUEST.value(),
				objectMapper);
			return;
		}

		String name = jwtProvider.getName(accessToken);
		String role = jwtProvider.getRole(accessToken);
		Long id = jwtProvider.getUserId(accessToken);

		CustomUserDetails userDetails = new CustomUserDetails(
			User.builder()
				.id(id)
				.name(name)
				.role(Role.valueOf(role))
				.build()
		);

		String refreshToken = jwtProvider.getRefreshToken(request);
		String clientUUID = request.getHeader("X-UUID");

		if (refreshToken == null) {
			if (clientUUID == null) {
				AuthResponse.failLogin(
					response,
					ApiResponse.of(false, HttpStatus.BAD_REQUEST, "탈취된 토큰으로 추정되어 로그인을 제한합니다."),
					HttpStatus.BAD_REQUEST.value(),
					objectMapper);
				return;
			}

			Object savedUUID = redisTemplate.opsForValue().get(AuthConstant.OAUTH2_LOGIN_UUID_PREFIX + id);

			if (savedUUID == null) {
				AuthResponse.failLogin(
					response,
					ApiResponse.of(false, HttpStatus.BAD_REQUEST, "로그인 인증시간이 만료되었습니다."),
					HttpStatus.BAD_REQUEST.value(),
					objectMapper);
				return;
			}

			if (!savedUUID.toString().equals(clientUUID)) {
				AuthResponse.failLogin(
					response,
					ApiResponse.of(false, HttpStatus.BAD_REQUEST, "로그인 uuid 가 달라 로그인을 제한합니다."),
					HttpStatus.BAD_REQUEST.value(),
					objectMapper);
				return;
			}

			Object savedRefreshToken = redisTemplate.opsForValue().get(AuthConstant.REDIS_TOKEN_PREFIX + id);

			if (savedRefreshToken == null) {
				String newRefreshToken = jwtProvider.createRefreshToken(userDetails, jwtConfig.getREFRESH_EXPIRATION());
				redisTemplate.opsForValue().set(
					AuthConstant.REDIS_TOKEN_PREFIX + id,
					newRefreshToken,
					jwtConfig.getREFRESH_EXPIRATION(),
					TimeUnit.MILLISECONDS
				);
				response.addCookie(CookieProvider.createRefreshTokenCookie(newRefreshToken,
					jwtConfig.getREFRESH_EXPIRATION()));
			} else {
				Long ttl = redisTemplate.getExpire(AuthConstant.REDIS_TOKEN_PREFIX + id, TimeUnit.MILLISECONDS);

				response.addCookie(CookieProvider.createRefreshTokenCookie(savedRefreshToken.toString(), ttl));
			}

			redisTemplate.delete(AuthConstant.OAUTH2_LOGIN_UUID_PREFIX + id);
		}

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,
			null, userDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authenticationToken);

		filterChain.doFilter(request, response);
	}

	private void reissueFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
		ServletException, IOException {
		String refreshToken = jwtProvider.getRefreshToken(request);

		if (refreshToken == null) {
			log.debug("존재하지 않는 RefreshToken");
			AuthResponse.failLogin(
				response,
				ApiResponse.of(false, HttpStatus.UNAUTHORIZED, "RefreshToken 이 존재하지 않습니다"),
				HttpStatus.UNAUTHORIZED.value(),
				objectMapper);
			return;
		}

		Long id = jwtProvider.getUserId(refreshToken);
		String name = jwtProvider.getName(refreshToken);
		String role = jwtProvider.getRole(refreshToken);

		Object savedRefreshToken = redisTemplate.opsForValue().get(AuthConstant.REDIS_TOKEN_PREFIX + id);

		if (savedRefreshToken == null || !savedRefreshToken.toString().equals(refreshToken)) {
			log.debug("유효하지 않은 RefreshToken");
			AuthResponse.failLogin(
				response,
				ApiResponse.of(false, HttpStatus.UNAUTHORIZED, "RefreshToken 이 유효하지 않습니다"),
				HttpStatus.UNAUTHORIZED.value(),
				objectMapper
			);
			return;
		}

		CustomUserDetails userDetails = new CustomUserDetails(
			User.builder()
				.id(id)
				.name(name)
				.role(Role.valueOf(role))
				.build()
		);

		String newAccessToken = AuthConstant.ACCESS_TOKEN_PREFIX + jwtProvider.createAccessToken(userDetails,
			jwtConfig.getACCESS_EXPIRATION());
		String newRefreshToken = jwtProvider.createRefreshToken(userDetails, jwtConfig.getREFRESH_EXPIRATION());

		redisTemplate.delete(AuthConstant.REDIS_TOKEN_PREFIX + userDetails.getUserId());
		redisTemplate.opsForValue().set(
			AuthConstant.REDIS_TOKEN_PREFIX + userDetails.getUserId(),
			newRefreshToken,
			jwtConfig.getREFRESH_EXPIRATION(),
			TimeUnit.MILLISECONDS
		);

		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authenticationToken);

		response.setHeader(AuthConstant.AUTHENTICATION, newAccessToken);
		response.addCookie(CookieProvider.createRefreshTokenCookie(newRefreshToken, jwtConfig.getREFRESH_EXPIRATION()));

		filterChain.doFilter(request, response);
	}
}