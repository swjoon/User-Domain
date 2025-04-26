package com.app.backend.global.config.security.util;

import org.springframework.stereotype.Component;

import com.app.backend.global.config.security.constant.AuthConstant;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CookieProvider {

	public Cookie createRefreshTokenCookie(final String refreshToken, final long expiration) {
		Cookie cookie = new Cookie(AuthConstant.REFRESH_TOKEN, refreshToken);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge((int)(expiration / 1000));
		return cookie;
	}

	public void expireRefreshTokenCookie(HttpServletResponse response) {
		Cookie cookie = new Cookie(AuthConstant.REFRESH_TOKEN, null);
		cookie.setMaxAge(0);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		response.addCookie(cookie);
	}
}
