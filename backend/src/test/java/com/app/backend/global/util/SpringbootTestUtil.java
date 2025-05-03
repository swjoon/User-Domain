package com.app.backend.global.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.app.backend.domain.user.repository.UserRepository;
import com.app.backend.domain.user.service.UserService;

import jakarta.persistence.EntityManager;

@SpringBootTest
@ActiveProfiles("test")
public abstract class SpringbootTestUtil {

	@Autowired
	protected PasswordEncoder passwordEncoder;

	@Autowired
	protected EntityManager em;

	@Autowired
	protected RedisTemplate<String, Object> redisTemplate;

	@Autowired
	protected UserService userService;

	@Autowired
	protected UserRepository userRepository;
}
