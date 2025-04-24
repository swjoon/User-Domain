package com.app.backend.domain.user.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import com.app.backend.domain.user.entity.User;
import com.app.backend.global.util.FixtureMonkeyUtil;
import com.app.backend.global.util.SpringbootTestUtil;

@Transactional
public class UserRepositoryTest extends SpringbootTestUtil {

	@AfterEach
	void cleanUp() {
		em.flush();
		em.clear();
	}

	@Test
	@DisplayName("[Repository] [성공] 유저 회원가입")
	void save() {
		// Given
		User givenUser = FixtureMonkeyUtil.createUser("testname");

		// When
		Long userId = userRepository.createUser(givenUser).getId();

		cleanUp();

		// Then
		User findUser = em.find(User.class, userId);

		assertThat(findUser).isNotNull();
		assertThat(findUser.getUsername()).isEqualTo(givenUser.getUsername());
		assertThat(findUser.getPassword()).isEqualTo(givenUser.getPassword());
		assertThat(findUser.getEmail()).isEqualTo(givenUser.getEmail());
		assertThat(findUser.getPhone()).isEqualTo(givenUser.getPhone());
		assertThat(findUser.getRole()).isEqualTo(givenUser.getRole());
	}

}
