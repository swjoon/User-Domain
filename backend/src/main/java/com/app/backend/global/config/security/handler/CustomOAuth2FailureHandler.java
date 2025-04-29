package com.app.backend.global.config.security.handler;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.app.backend.global.config.security.util.AuthResponse;
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
public class CustomOAuth2FailureHandler implements AuthenticationFailureHandler {

	private final ObjectMapper objectMapper;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException, ServletException {

		log.debug("OAuth2 로그인 실패", exception);

		AuthResponse.failLogin(
			response,
			ApiResponse.of(false, HttpStatus.BAD_REQUEST, "소셜로그인에 실패했습니다."),
			HttpStatus.BAD_REQUEST.value(),
			objectMapper);
	}
}
