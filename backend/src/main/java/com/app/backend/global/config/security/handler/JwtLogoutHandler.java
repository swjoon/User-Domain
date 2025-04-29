package com.app.backend.global.config.security.handler;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.app.backend.global.config.security.util.CookieProvider;
import com.app.backend.global.config.security.util.JwtProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

	private final JwtProvider jwtProvider;
	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		String authorization = request.getHeader("Authorization");
		String accessToken = null;

		String refreshToken = jwtProvider.getRefreshToken(request);

		if (authorization != null && refreshToken != null) {
			accessToken = authorization.substring(7);
			try {
				String username = jwtProvider.getUsername(accessToken);
				Date expiration = jwtProvider.getExpirationDate(accessToken);
				long duration = expiration.getTime() - System.currentTimeMillis();

				redisTemplate.opsForValue().set(accessToken, "Logout", duration, TimeUnit.MILLISECONDS);
				redisTemplate.delete(username);
			} catch (Exception e) {
				log.debug(e.getMessage());
			} finally {
				CookieProvider.expireRefreshTokenCookie(response);
			}
		}
	}
}
