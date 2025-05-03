package com.app.backend.domain.user.mapper;

import com.app.backend.domain.user.dto.request.CreateUserOauth2Dto;
import com.app.backend.domain.user.entity.Provider;
import com.app.backend.domain.user.entity.User;
import com.app.backend.global.config.security.constant.AuthConstant;
import com.app.backend.global.config.security.info.domain.OAuth2ProviderInfo;

public class UserMapper {

	public static User fromOAuth2Create(final CreateUserOauth2Dto requestDto, final OAuth2ProviderInfo info) {

		String phoneNumber = info.getProvider().equals(AuthConstant.GOOGLE) ?
			requestDto.getPhoneNumber() : info.getPhone();

		return User.builder()
			.username(info.getProviderId())
			.name(info.getName())
			.nickname(requestDto.getNickName())
			.email(info.getEmail())
			.phone(phoneNumber)
			.gender(requestDto.getGender())
			.birthDate(requestDto.getBirthDate())
			.zipCode(requestDto.getZipCode())
			.address(requestDto.getAddress())
			.detailAddress(requestDto.getDetailAddress())
			.provider(Provider.valueOf(info.getProvider()))
			.providerId(info.getProviderId())
			.profileImageUrl(info.getProfileImage())
			.build();
	}
}
