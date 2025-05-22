package com.app.backend.global.config.security.service.auth;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.app.backend.domain.user.dto.request.CreateUserOauth2Dto;
import com.app.backend.domain.user.entity.User;
import com.app.backend.domain.user.exception.UserErrorCode;
import com.app.backend.domain.user.exception.UserException;
import com.app.backend.domain.user.service.UserService;
import com.app.backend.global.config.security.config.JwtConfig;
import com.app.backend.global.config.security.constant.AuthConstant;
import com.app.backend.global.config.security.dto.response.LoginResponseDto;
import com.app.backend.global.config.security.info.CustomUserDetails;
import com.app.backend.global.config.security.util.AuthResponse;
import com.app.backend.global.config.security.util.CookieProvider;
import com.app.backend.global.config.security.util.JwtProvider;
import com.app.backend.global.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final JwtConfig jwtConfig;
	private final JwtProvider jwtProvider;
	private final ObjectMapper objectMapper;
	private final RedisTemplate<String, Object> redisTemplate;

	private final UserService userService;

	@Override
	public void oAuth2SignUpAndSignIn(final CreateUserOauth2Dto requestDto, final HttpServletResponse response) {
		try {

			User user = userService.createOAuth2User(requestDto);

			CustomUserDetails userDetails = new CustomUserDetails(user);

			String accessToken = AuthConstant.ACCESS_TOKEN_PREFIX + jwtProvider.createAccessToken(userDetails,
				jwtConfig.getACCESS_EXPIRATION());

			String refreshToken = jwtProvider.createRefreshToken(userDetails, jwtConfig.getREFRESH_EXPIRATION());

			redisTemplate.opsForValue().set(
				AuthConstant.REDIS_TOKEN_PREFIX + user.getId(),
				refreshToken,
				jwtConfig.getREFRESH_EXPIRATION(),
				TimeUnit.MILLISECONDS
			);

			LoginResponseDto responseDto = new LoginResponseDto(user.getId(), user.getName());

			Cookie cookie = CookieProvider.createRefreshTokenCookie(refreshToken, jwtConfig.getREFRESH_EXPIRATION());

			redisTemplate.delete(requestDto.getUuid());

			AuthResponse.successLogin(
				response,
				accessToken,
				cookie,
				HttpStatus.CREATED.value(),
				ApiResponse.of(true, HttpStatus.CREATED, "OAuth2 회원가입을 완료했습니다.", responseDto),
				objectMapper
			);
		} catch (UserException e) {
			log.debug("OAuth2 회원가입 오류", e);
			throw e;
		} catch (Exception e) {
			log.debug("OAuth2 회원가입 중 예상치 못한 오류", e);
			throw new UserException(UserErrorCode.OAUTH2_SIGNUP_FAILED);
		}
	}
}
