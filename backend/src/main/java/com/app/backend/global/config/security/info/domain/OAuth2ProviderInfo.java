package com.app.backend.global.config.security.info.domain;

import com.app.backend.global.config.security.info.OAuth2UserInfo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OAuth2ProviderInfo {
	String provider;
	String providerId;
	String name;
	String email;
	String phone;
	String profileImage;

	public static OAuth2ProviderInfo from(OAuth2UserInfo info) {
		return OAuth2ProviderInfo.builder()
			.provider(info.getProvider())
			.providerId(info.getProviderId())
			.name(info.getName())
			.email(info.getEmail())
			.phone(info.getPhone())
			.profileImage(info.getProfileImage())
			.build();
	}
}
