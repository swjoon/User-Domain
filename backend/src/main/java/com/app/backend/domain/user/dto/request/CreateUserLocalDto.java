package com.app.backend.domain.user.dto.request;

import java.time.LocalDate;

import com.app.backend.domain.user.entity.Gender;
import com.app.backend.domain.user.entity.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserLocalDto {

	@NotBlank
	@Size(min = 5, max = 20)
	@Pattern(regexp = "^[^\\s]+$", message = "공백 없이 입력해주세요.")
	private String username;

	@NotBlank
	@Size(min = 8, max = 14)
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+]{8,}$", message = "비밀번호는 영문, 숫자를 포함해야 합니다.")
	private String password;

	@NotBlank
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	private String email;

	@NotBlank
	@Size(min = 2, max = 30)
	private String name;

	@NotBlank
	@Pattern(regexp = "^01[016789][0-9]{7,8}$", message = "올바른 휴대폰 번호 형식이 아닙니다.")
	private String phone;

	@NotNull
	private Gender gender;

	@NotNull
	private LocalDate birthDate;

	public static User toEntity(final CreateUserLocalDto createUserDto, final String encodedPassword) {
		return User.builder()
			.username(createUserDto.getUsername())
			.password(encodedPassword)
			.email(createUserDto.getEmail())
			.name(createUserDto.getName())
			.phone(createUserDto.getPhone())
			.gender(createUserDto.getGender())
			.birthDate(createUserDto.getBirthDate())
			.build();
	}
}
