package com.app.backend.global.config.security.info.domain;

import java.util.Map;

import com.app.backend.global.config.security.info.OAuth2UserInfo;

public class KakaoOAuth2UserInfo implements OAuth2UserInfo {

	private final Map<String, Object> attributes;

	public KakaoOAuth2UserInfo(final Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String getProvider() {
		return "kakao";
	}

	@Override
	public String getProviderId() {
		return String.valueOf(attributes.get("id"));
	}

	@Override
	public String getName() {
		Map<String, Object> account = (Map<String, Object>)attributes.get("kakao_account");
		return (String)account.get("name");
	}

	@Override
	public String getEmail() {
		Map<String, Object> account = (Map<String, Object>)attributes.get("kakao_account");
		return (String)account.get("email");
	}

	@Override
	public String getPhone() {
		Map<String, Object> account = (Map<String, Object>)attributes.get("kakao_account");
		return (String)account.get("phone_number");
	}

	@Override
	public String getProfileImage() {
		Map<String, Object> account = (Map<String, Object>)attributes.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>)account.get("profile");
		return (String)profile.get("profile_image_url");
	}
}
