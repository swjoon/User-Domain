package com.app.backend.global.config.security.info;

public interface OAuth2UserInfo {
	String getProvider();
	String getProviderId();
	String getName();
	String getEmail();
	String getPhone();
	String getProfileImage();
}
