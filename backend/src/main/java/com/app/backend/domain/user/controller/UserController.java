package com.app.backend.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.app.backend.domain.user.dto.request.CreateUserLocalDto;
import com.app.backend.domain.user.dto.request.CreateUserOauth2Dto;
import com.app.backend.domain.user.service.UserService;
import com.app.backend.global.config.security.dto.response.LoginResponseDto;
import com.app.backend.global.config.security.service.auth.AuthService;
import com.app.backend.global.response.ApiResponse;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final AuthService authService;
	private final UserService userService;

	@PostMapping("/signup")
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<LoginResponseDto> signUp(
		@Valid @RequestBody final CreateUserLocalDto requestDto
	) {

		LoginResponseDto response = userService.createUser(requestDto);

		return ApiResponse.of(true, HttpStatus.CREATED, "회원가입을 완료했습니다.", response);
	}

	@PostMapping("/signup/oauth2")
	@ResponseStatus(HttpStatus.CREATED)
	public void oauthSignUp(
		@Valid @RequestBody final CreateUserOauth2Dto requestDto,
		final HttpServletResponse response
	) {

		authService.oAuth2SignUpAndSignIn(requestDto, response);
	}

}
