package com.app.backend.global.config.security.dto.response;

import com.app.backend.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponseDto {
	private Long id;
	private String name;

	public static LoginResponseDto from(final Long id, final String name) {
		return LoginResponseDto.builder()
			.id(id)
			.name(name)
			.build();
	}
}
