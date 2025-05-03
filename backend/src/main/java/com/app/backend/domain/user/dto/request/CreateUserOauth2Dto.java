package com.app.backend.domain.user.dto.request;

import java.time.LocalDate;

import com.app.backend.domain.user.entity.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserOauth2Dto {

	private String phoneNumber;

	@NotBlank
	@Size(max = 30)
	private String nickName;

	@NotNull
	private Gender gender;

	@NotNull
	private LocalDate birthDate;

	@NotBlank
	private String zipCode;

	@NotBlank
	private String address;

	@NotBlank
	private String detailAddress;

	@NotBlank
	private String uuid;
}
