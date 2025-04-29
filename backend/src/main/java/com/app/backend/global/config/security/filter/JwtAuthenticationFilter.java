package com.app.backend.global.config.security.filter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.app.backend.domain.user.exception.UserException;
import com.app.backend.global.config.security.config.JwtConfig;
import com.app.backend.global.config.security.dto.request.LoginUserDto;
import com.app.backend.global.config.security.info.CustomUserDetails;
import com.app.backend.global.config.security.util.AuthResponse;
import com.app.backend.global.config.security.util.CookieProvider;
import com.app.backend.global.config.security.util.JwtProvider;
import com.app.backend.global.error.exception.GlobalErrorCode;
import com.app.backend.global.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final JwtConfig jwtConfig;
	private final JwtProvider jwtProvider;
	private final ObjectMapper objectMapper;
	private final RedisTemplate<String, Object> redisTemplate;
	private final AuthenticationManager authenticationManager;
	private final Validator validator;

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
		throws AuthenticationException {

		try {
			LoginUserDto loginRequest = objectMapper.readValue(request.getInputStream(), LoginUserDto.class);

			validateDto(loginRequest);

			String username = loginRequest.getUsername();
			String password = loginRequest.getPassword();

			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

			return authenticationManager.authenticate(authToken);
		} catch (IOException e) {
			throw new AuthenticationServiceException("로그인 정보가 정확하지 않습니다");
		}
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException {
		AuthResponse.failLogin(
			response,
			ApiResponse.of(false, HttpStatus.BAD_REQUEST, "로그인 정보가 정확하지 않습니다"),
			HttpStatus.BAD_REQUEST.value(),
			objectMapper);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authentication) throws IOException {

		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();

		String accessToken = jwtProvider.createAccessToken(userDetails, jwtConfig.getACCESS_EXPIRATION());
		String refreshToken = jwtProvider.createRefreshToken(userDetails, jwtConfig.getREFRESH_EXPIRATION());

		redisTemplate.delete(userDetails.getUsername());
		redisTemplate.opsForValue().set(
			userDetails.getUsername(),
			refreshToken,
			jwtConfig.getREFRESH_EXPIRATION(),
			TimeUnit.MILLISECONDS
		);

		AuthResponse.successLogin(
			response,
			accessToken,
			CookieProvider.createRefreshTokenCookie(refreshToken, jwtConfig.getREFRESH_EXPIRATION()),
			HttpStatus.OK.value(),
			ApiResponse.of(true, HttpStatus.OK, "로그인 성공"),
			objectMapper);
	}

	private void validateDto(LoginUserDto loginRequest) throws IOException {
		Set<ConstraintViolation<LoginUserDto>> violations = validator.validate(loginRequest);

		if (!violations.isEmpty()) {
			throw new UserException(GlobalErrorCode.INVALID_INPUT_VALUE);
		}
	}
}
