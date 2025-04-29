package com.app.backend.global.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class AppConfig {

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer addJavaTimeModule() {
		return builder -> {
			builder.modules(new JavaTimeModule());
			builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		};
	}
}
