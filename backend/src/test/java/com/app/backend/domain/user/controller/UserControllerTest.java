package com.app.backend.domain.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.app.backend.domain.user.dto.request.CreateUserOauth2Dto;
import com.app.backend.global.config.security.constant.AuthConstant;
import com.app.backend.global.config.security.info.domain.GoogleOAuth2UserInfo;
import com.app.backend.global.config.security.info.domain.OAuth2ProviderInfo;
import com.app.backend.global.util.FixtureMonkeyUtil;
import com.app.backend.global.util.WebMvcTestUtil;

public class UserControllerTest extends WebMvcTestUtil {

	private static final String UUID = "TEST_UUID";

	@AfterEach
	void cleanUp() {
		redisTemplate.delete(UUID);
	}

	@Test
	@DisplayName("[Controller] [성공] OAuth2 회원가입")
	void saveOAuth2() throws Exception {
		// Given
		String providerId = "google-123";

		Map<String, Object> attributes = new HashMap<>();

		attributes.put("sub", providerId);
		attributes.put("name", "name");
		attributes.put("email", "test@gmail.com");
		attributes.put("picture", "test");

		OAuth2ProviderInfo info = OAuth2ProviderInfo.from(new GoogleOAuth2UserInfo(attributes));

		redisTemplate.opsForValue().set(UUID, info);

		CreateUserOauth2Dto requestDto = FixtureMonkeyUtil.fixtureMonkeyBuilder
			.giveMeBuilder(CreateUserOauth2Dto.class)
			.set("uuid", UUID)
			.set("nickName", "tester")
			.set("phoneNumber", "01012345678")
			.sample();

		String requestBody = objectMapper.writeValueAsString(requestDto);

		// When
		ResultActions resultActions = mockMvc.perform(post("/api/v1/users/signup/oauth2")
											 .accept(MediaType.APPLICATION_JSON_VALUE)
											 .contentType(MediaType.APPLICATION_JSON)
											 .content(requestBody));

		// Then
		resultActions.andExpect(status().isCreated())
										.andExpect(header().exists("Authorization"))
										.andExpect(cookie().exists(AuthConstant.REFRESH_TOKEN))
										.andReturn();
	}
}
