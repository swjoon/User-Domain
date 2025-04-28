package com.app.backend.global.config.security.dto.response;

import com.app.backend.domain.user.entity.Provider;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProviderDataDto {

	private String uuid;
	private Provider provider;

}
