package com.app.backend.global.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.app.backend.domain.user.controller.UserController;
import com.app.backend.domain.user.service.UserService;
import com.app.backend.global.config.security.service.auth.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class WebMvcTestUtil {

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected EntityManager em;

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	protected RedisTemplate<String, Object> redisTemplate;
}
