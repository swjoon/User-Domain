package com.app.backend.global.config.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
public class JwtConfig {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.token.access-expiration}")
	private long ACCESS_EXPIRATION;

	@Value("${jwt.token.refresh-expiration}")
	private long REFRESH_EXPIRATION;
}
