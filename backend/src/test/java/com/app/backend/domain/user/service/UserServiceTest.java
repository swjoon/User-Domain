package com.app.backend.domain.user.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import com.app.backend.domain.user.dto.request.CreateUserLocalDto;
import com.app.backend.domain.user.dto.request.CreateUserOauth2Dto;
import com.app.backend.domain.user.entity.Gender;
import com.app.backend.domain.user.entity.Provider;
import com.app.backend.domain.user.entity.User;
import com.app.backend.domain.user.exception.UserErrorCode;
import com.app.backend.domain.user.exception.UserException;
import com.app.backend.global.config.security.dto.response.LoginResponseDto;
import com.app.backend.global.config.security.info.domain.GoogleOAuth2UserInfo;
import com.app.backend.global.config.security.info.domain.KakaoOAuth2UserInfo;
import com.app.backend.global.config.security.info.domain.NaverOAuth2UserInfo;
import com.app.backend.global.config.security.info.domain.OAuth2ProviderInfo;
import com.app.backend.global.util.FixtureMonkeyUtil;
import com.app.backend.global.util.SpringbootTestUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
public class UserServiceTest extends SpringbootTestUtil {

	private static final String UUID = "TEST_UUID";

	@AfterEach
	void cleanUp() {
		redisTemplate.delete(UUID);
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
		LoginResponseDto response = userService.createUser(requestDto);

		cleanUp();

		// Then
		User user = em.find(User.class, response.getId());

		assertThat(user.getUsername()).isEqualTo(requestDto.getUsername());
		assertThat(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())).isTrue();
		assertThat(user.getName()).isEqualTo(requestDto.getName());
		assertThat(user.getEmail()).isEqualTo(requestDto.getEmail());
		assertThat(user.getPhone()).isEqualTo(requestDto.getPhone());
	}

	@Test
	@DisplayName("[Service] [성공] OAuth2 유저 회원가입 - GOOGLE 회원가입")
	void saveOAuth2_GOOGLE() {
		// Given
		String providerId = "google-123";

		Map<String, Object> attributes = new HashMap<>();

		attributes.put("sub", providerId);
		attributes.put("name", "name");
		attributes.put("email", "test@gmail.com");
		attributes.put("picture", "test");

		OAuth2ProviderInfo info = OAuth2ProviderInfo
			.from(new GoogleOAuth2UserInfo(attributes));

		redisTemplate.opsForValue().set(UUID, info);

		CreateUserOauth2Dto requestDto = FixtureMonkeyUtil.fixtureMonkeyBuilder
			.giveMeBuilder(CreateUserOauth2Dto.class)
			.set("nickName", "test name")
			.set("phoneNumber", "01012345678")
			.set("uuid", UUID)
			.sample();

		// When
		User response = userService.createOAuth2User(requestDto);

		cleanUp();

		// Then

		User user = em.find(User.class, response.getId());

		assertThat(user.getName()).isEqualTo(info.getName());
		assertThat(user.getProvider()).isEqualTo(Provider.GOOGLE);
		assertThat(user.getProviderId()).isEqualTo(info.getProviderId());
	}

	@Test
	@DisplayName("[Service] [성공] OAuth2 유저 회원가입 - NAVER 회원가입")
	void saveOAuth2_NAVER() {
		// Given
		String providerId = "naver-123";

		Map<String, Object> attributes = new HashMap<>();
		Map<String, Object> map = new HashMap<>();

		map.put("id", providerId);
		map.put("name", "name");
		map.put("email", "test@gmail.com");
		map.put("mobile", "01012345678");
		map.put("profile_image", "test");

		attributes.put("response", map);

		OAuth2ProviderInfo info = OAuth2ProviderInfo
			.from(new NaverOAuth2UserInfo(attributes));

		redisTemplate.opsForValue().set(UUID, info);

		CreateUserOauth2Dto requestDto = FixtureMonkeyUtil.fixtureMonkeyBuilder
			.giveMeBuilder(CreateUserOauth2Dto.class)
			.set("nickName", "test name")
			.set("phoneNumber", null)
			.set("uuid", UUID)
			.sample();

		// When
		User response = userService.createOAuth2User(requestDto);

		cleanUp();

		// Then

		User user = em.find(User.class, response.getId());

		assertThat(user.getName()).isEqualTo(info.getName());
		assertThat(user.getProvider()).isEqualTo(Provider.NAVER);
		assertThat(user.getProviderId()).isEqualTo(info.getProviderId());
	}

	@Test
	@DisplayName("[Service] [성공] OAuth2 유저 회원가입 - KAKAO 회원가입")
	void saveOAuth2_KAKAO() {
		// Given
		String providerId = "kakao-123";

		Map<String, Object> attributes = new HashMap<>();
		Map<String, Object> kakao_account = new HashMap<>();
		Map<String, Object> profile = new HashMap<>();

		profile.put("profile_image_url", "test");

		kakao_account.put("name", "name");
		kakao_account.put("email", "test@gmail.com");
		kakao_account.put("phone_number", "01012345678");
		kakao_account.put("profile", profile);

		attributes.put("id", providerId);
		attributes.put("kakao_account", kakao_account);

		OAuth2ProviderInfo info = OAuth2ProviderInfo
			.from(new KakaoOAuth2UserInfo(attributes));

		redisTemplate.opsForValue().set(UUID, info);

		CreateUserOauth2Dto requestDto = FixtureMonkeyUtil.fixtureMonkeyBuilder
			.giveMeBuilder(CreateUserOauth2Dto.class)
			.set("nickName", "test name")
			.set("phoneNumber", "01012345678")
			.set("uuid", UUID)
			.sample();

		// When
		User response = userService.createOAuth2User(requestDto);

		cleanUp();

		// Then

		User user = em.find(User.class, response.getId());

		assertThat(user.getName()).isEqualTo(info.getName());
		assertThat(user.getProvider()).isEqualTo(Provider.KAKAO);
		assertThat(user.getProviderId()).isEqualTo(info.getProviderId());
	}

	@Test
	@DisplayName("[Service] [실패] OAuth2 유저 회원가입 - UUID 없음")
	void saveOAuth2_UUID() {
		// Given
		CreateUserOauth2Dto requestDto = FixtureMonkeyUtil.fixtureMonkeyBuilder
			.giveMeBuilder(CreateUserOauth2Dto.class)
			.set("nickName", "test name")
			.set("phoneNumber", "01012345678")
			.set("uuid", UUID)
			.sample();

		// When & Then
		assertThatThrownBy(() -> userService.createOAuth2User(requestDto))
			.isInstanceOf(UserException.class)
			.hasFieldOrPropertyWithValue("domainErrorCode", UserErrorCode.UUID_NOT_FOUND)
			.hasMessageContaining(UserErrorCode.UUID_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("[Service] [실패] OAuth2 유저 회원가입 - GOOGLE 전화번호 NULL")
	void saveOAuth2_GOOGLE_NULL() {
		// Given
		String providerId = "google-123";

		Map<String, Object> attributes = new HashMap<>();

		attributes.put("sub", providerId);
		attributes.put("name", "name");
		attributes.put("email", "test@gmail.com");
		attributes.put("picture", "test");

		OAuth2ProviderInfo info = OAuth2ProviderInfo
			.from(new GoogleOAuth2UserInfo(attributes));

		redisTemplate.opsForValue().set(UUID, info);

		CreateUserOauth2Dto requestDto = FixtureMonkeyUtil.fixtureMonkeyBuilder
			.giveMeBuilder(CreateUserOauth2Dto.class)
			.set("nickName", "test name")
			.set("phoneNumber", null)
			.set("uuid", UUID)
			.sample();

		// When & Then
		assertThatThrownBy(() -> userService.createOAuth2User(requestDto))
			.isInstanceOf(UserException.class)
			.hasFieldOrPropertyWithValue("domainErrorCode", UserErrorCode.PHONE_NUMBER_NOT_FOUND)
			.hasMessageContaining(UserErrorCode.PHONE_NUMBER_NOT_FOUND.getMessage());
	}
}
