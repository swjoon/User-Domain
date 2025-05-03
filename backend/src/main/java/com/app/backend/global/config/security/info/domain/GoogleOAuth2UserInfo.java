package com.app.backend.global.config.security.info.domain;

import java.util.Map;

import com.app.backend.global.config.security.constant.AuthConstant;
import com.app.backend.global.config.security.info.OAuth2UserInfo;

public class GoogleOAuth2UserInfo implements OAuth2UserInfo {

	private final Map<String, Object> attributes;

	public GoogleOAuth2UserInfo(final Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String getProvider() {
		return AuthConstant.GOOGLE;
	}

	@Override
	public String getProviderId() {
		return (String)attributes.get("sub");
	}

	@Override
	public String getName() {
		return (String)attributes.get("name");
	}

	@Override
	public String getEmail() {
		return (String)attributes.get("email");
	}

	@Override
	public String getPhone() {
		return null;
	}

	@Override
	public String getProfileImage() {
		return (String)attributes.get("picture");
	}
}
