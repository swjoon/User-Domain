package com.app.backend.global.config.security.handler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.app.backend.global.config.security.config.JwtConfig;
import com.app.backend.global.config.security.dto.response.ProviderDataDto;
import com.app.backend.global.config.security.info.CustomUserDetails;
import com.app.backend.global.config.security.util.AuthResponse;
import com.app.backend.global.config.security.util.CookieProvider;
import com.app.backend.global.config.security.util.JwtProvider;
import com.app.backend.global.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private final ObjectMapper objectMapper;

	private final JwtConfig jwtConfig;
	private final JwtProvider jwtProvider;

	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();

		if (userDetails.getNeedSignup()) {
			ProviderDataDto data = ProviderDataDto.builder()
				.uuid(userDetails.getUUID())
				.provider(userDetails.getProvider())
				.build();

			AuthResponse.defaultResponse(
				response,
				ApiResponse.of(true, HttpStatus.OK, "추가정보를 기입해주세요.", data),
				HttpStatus.OK.value(),
				objectMapper
			);

			return;
		}

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
			objectMapper
		);
	}
}
