package com.app.backend.global.config.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.app.backend.global.config.security.filter.JwtAuthenticationFilter;
import com.app.backend.global.config.security.filter.JwtAuthorizationFilter;
import com.app.backend.global.config.security.handler.CustomAccessDeniedHandler;
import com.app.backend.global.config.security.handler.CustomAuthenticationEntryPoint;
import com.app.backend.global.config.security.handler.CustomOAuth2FailureHandler;
import com.app.backend.global.config.security.handler.CustomOAuth2SuccessHandler;
import com.app.backend.global.config.security.handler.JwtLogoutHandler;
import com.app.backend.global.config.security.handler.JwtLogoutSuccessHandler;
import com.app.backend.global.config.security.service.CustomOAuth2UserService;
import com.app.backend.global.config.security.util.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final Validator validator;
	private final ObjectMapper objectMapper;
	private final RedisTemplate<String, Object> redisTemplate;

	private final JwtConfig jwtConfig;
	private final JwtProvider jwtProvider;
	private final CorsConfig corsConfig;

	private final JwtLogoutHandler jwtLogoutHandler;
	private final JwtLogoutSuccessHandler jwtLogoutSuccessHandler;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	private final CustomAccessDeniedHandler customAccessDeniedHandler;
	private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
	private final CustomOAuth2FailureHandler customOAuth2FailureHandler;

	private final CustomOAuth2UserService customOAuth2UserService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationConfiguration configuration)
		throws Exception {

		// 로그인 필터
		JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
			jwtConfig,
			jwtProvider,
			objectMapper,
			redisTemplate,
			authenticationManager(configuration),
			validator
		);

		// 로그인 경로 url
		jwtAuthenticationFilter.setFilterProcessesUrl("/api/v1/users/login");

		JwtAuthorizationFilter jwtAuthorizationFilter = new JwtAuthorizationFilter(
			jwtConfig,
			jwtProvider,
			objectMapper,
			redisTemplate
		);

		http.headers(headers -> headers.frameOptions(option -> option.sameOrigin()))

			.csrf(AbstractHttpConfigurer::disable)
			.cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))

			.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

				.requestMatchers(
					"/h2-console/**",
					"/api/v1/users/signup/**",
					"/oauth2/**",
					"/login/oauth2/**")
				.permitAll()

				.anyRequest().authenticated()
			)

			.oauth2Login(oauth2 -> oauth2
				.successHandler(customOAuth2SuccessHandler)
				.failureHandler(customOAuth2FailureHandler)
				.userInfoEndpoint(info -> info.userService(customOAuth2UserService))
			)

			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

			.addFilterAt(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(jwtAuthorizationFilter, JwtAuthenticationFilter.class)

			.exceptionHandling(ex -> ex
				.authenticationEntryPoint(customAuthenticationEntryPoint)
				.accessDeniedHandler(customAccessDeniedHandler)
			)

			.logout(logout -> logout
				.logoutUrl("/api/v1/logout")
				.addLogoutHandler(jwtLogoutHandler)
				.logoutSuccessHandler(jwtLogoutSuccessHandler)
			);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
		throws Exception {

		return authenticationConfiguration.getAuthenticationManager();
	}
}
