package com.app.backend.global.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.app.backend.domain.user.repository.UserRepository;
import com.app.backend.domain.user.service.UserService;
import com.app.backend.global.config.TestOAuth2Config;

import jakarta.persistence.EntityManager;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestOAuth2Config.class)
public abstract class SpringbootTestUtil {

	@Autowired
	protected PasswordEncoder passwordEncoder;

	@Autowired
	protected EntityManager em;

	@Autowired
	protected UserService userService;

	@Autowired
	protected UserRepository userRepository;
}
