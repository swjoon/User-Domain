package com.app.backend.global.config.security.util;

import java.io.IOException;

import com.app.backend.global.config.security.constant.AuthConstant;
import com.app.backend.global.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class AuthResponse {

	public static void success(
		final HttpServletResponse response,
		final String accessToken,
		final Cookie cookie,
		final int status,
		final ApiResponse<?> apiResponse,
		final ObjectMapper om) throws IOException {

		defaultResponse(response, apiResponse, status, om);

		response.setHeader(AuthConstant.AUTHENTICATION, accessToken);
		response.addCookie(cookie);
	}

	public static void failLogin(
		final HttpServletResponse response,
		final ApiResponse<?> apiResponse,
		final int status,
		final ObjectMapper om) throws IOException {

		defaultResponse(response, apiResponse, status, om);
	}

	private static void defaultResponse(
		final HttpServletResponse response,
		final ApiResponse<?> apiResponse,
		final int status,
		final ObjectMapper om) throws IOException {

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(status);
		response.getWriter().write(om.writeValueAsString(apiResponse));
	}
}
