package com.app.backend.global.config.security.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import com.app.backend.global.config.security.config.JwtConfig;
import com.app.backend.global.config.security.constant.AuthConstant;
import com.app.backend.global.config.security.info.CustomUserDetails;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtProvider {
	private final SecretKey secretKey;

	public JwtProvider(JwtConfig jwtConfig) {
		this.secretKey = new SecretKeySpec(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8),
			Jwts.SIG.HS512.key().build().getAlgorithm());
	}

	public Long getUserId(String token) {
		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("id", Long.class);
	}

	public String getUsername(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.get("username", String.class);
	}

	public String getRole(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.get("role", String.class);
	}

	public boolean isExpired(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.getExpiration()
			.before(new Date());
	}

	public Date getExpirationDate(String token) {
		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration();
	}

	public String getRefreshToken(HttpServletRequest request) {
		if (request.getCookies() == null) {
			return null;
		}

		return Arrays.stream(request.getCookies())
			.filter(cookie -> AuthConstant.REFRESH_TOKEN.equals(cookie.getName()))
			.findFirst()
			.map(Cookie::getValue)
			.orElse(null);
	}

	public String createAccessToken(CustomUserDetails customUserDetails, long expiration) {
		long currentTime = System.currentTimeMillis();

		return "Bearer " + Jwts.builder()
			.claim("subject", "access")
			.claim("id", customUserDetails.getUserId())
			.claim("username", customUserDetails.getUsername())
			.claim("role", customUserDetails.getRole())
			.issuedAt(new Date(currentTime))
			.expiration(new Date(currentTime + expiration))
			.signWith(secretKey)
			.compact();
	}

	public String createRefreshToken(CustomUserDetails customUserDetails, long expiration) {
		long currentTime = System.currentTimeMillis();

		return Jwts.builder()
			.claim("subject", "refresh")
			.claim("id", customUserDetails.getUserId())
			.claim("username", customUserDetails.getUsername())
			.claim("role", customUserDetails.getRole())
			.issuedAt(new Date(currentTime))
			.expiration(new Date(currentTime + expiration))
			.signWith(secretKey)
			.compact();
	}
}
