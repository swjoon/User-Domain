package com.app.backend.global.config.security.handler;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.app.backend.global.config.security.config.JwtConfig;
import com.app.backend.global.config.security.constant.AuthConstant;
import com.app.backend.global.config.security.info.CustomUserDetails;
import com.app.backend.global.config.security.util.JwtProvider;
import com.app.backend.global.util.UuidUtil;
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

	private static final String REDIRECT_URI = "http://localhost:3000/oauth2/callback";

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
			String redirectUrl = UriComponentsBuilder.fromUriString(REDIRECT_URI)
				.queryParam(AuthConstant.UUID, userDetails.getUUID())
				.queryParam(AuthConstant.PROVIDER, userDetails.getProvider())
				.build().toUriString();

			response.sendRedirect(redirectUrl);

			return;
		}

		String accessToken = jwtProvider.createAccessToken(userDetails, jwtConfig.getACCESS_EXPIRATION());

		String uuid = UuidUtil.getUuid(LocalDate.now().toString() + userDetails.getUserId(), 7);

		redisTemplate.opsForValue()
			.set(AuthConstant.OAUTH2_LOGIN_UUID_PREFIX + userDetails.getUserId(), uuid , 1, TimeUnit.MINUTES);

		String redirectUrl = UriComponentsBuilder.fromUriString(REDIRECT_URI)
			.queryParam(AuthConstant.ACCESS_TOKEN, accessToken)
			.queryParam(AuthConstant.UUID, uuid)
			.build()
			.toUriString();

		response.sendRedirect(redirectUrl);
	}
}
