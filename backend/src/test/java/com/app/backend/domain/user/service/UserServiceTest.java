package com.app.backend.domain.user.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import com.app.backend.domain.user.dto.request.CreateUserLocalDto;
import com.app.backend.domain.user.entity.Gender;
import com.app.backend.domain.user.entity.User;
import com.app.backend.global.util.FixtureMonkeyUtil;
import com.app.backend.global.util.SpringbootTestUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
public class UserServiceTest extends SpringbootTestUtil {

	@AfterEach
	void cleanUp() {
		em.flush();
		em.clear();
	}

	@Test
	@DisplayName("[Service] [성공] 유저 회원가입")
	void save() {
		// given
		CreateUserLocalDto requestDto = FixtureMonkeyUtil.fixtureMonkeyBuilder
			.giveMeBuilder(CreateUserLocalDto.class)
			.set("username", "testname")
			.set("password", "12345678ab")
			.set("email", "test@test.com")
			.set("name", "test")
			.set("phone", "01012345678")
			.set("gender", Gender.MALE)
			.set("birthDate", LocalDate.of(1997, 1, 1))
			.sample();

		// When
		long userId = userService.createUser(requestDto);

		cleanUp();

		// Then
		User user = em.find(User.class, userId);

		assertThat(user.getUsername()).isEqualTo(requestDto.getUsername());
		assertThat(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())).isTrue();
		assertThat(user.getName()).isEqualTo(requestDto.getName());
		assertThat(user.getEmail()).isEqualTo(requestDto.getEmail());
		assertThat(user.getPhone()).isEqualTo(requestDto.getPhone());
	}
}
