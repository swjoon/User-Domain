package com.app.backend.global.config.security.info.domain;

import java.util.Map;

import com.app.backend.global.config.security.constant.AuthConstant;
import com.app.backend.global.config.security.info.OAuth2UserInfo;

public class NaverOAuth2UserInfo implements OAuth2UserInfo {

	private final Map<String, Object> attributes;

	public NaverOAuth2UserInfo(final Map<String, Object> attributes) {
		this.attributes = (Map<String, Object>) attributes.get("response");
	}

	@Override
	public String getProvider() {
		return AuthConstant.NAVER;
	}

	@Override
	public String getProviderId() {
		return (String) attributes.get("id");
	}

	@Override
	public String getName() {
		return (String) attributes.get("name");
	}

	@Override
	public String getEmail() {
		return (String) attributes.get("email");
	}

	@Override
	public String getPhone() {
		return (String) attributes.get("mobile");
	}

	@Override
	public String getProfileImage() {
		return (String) attributes.get("profile_image");
	}
}
