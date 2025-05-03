package com.app.backend.domain.user.service;

import com.app.backend.domain.user.dto.request.CreateUserLocalDto;
import com.app.backend.domain.user.dto.request.CreateUserOauth2Dto;
import com.app.backend.domain.user.entity.User;
import com.app.backend.global.config.security.dto.response.LoginResponseDto;

public interface UserService {

	/**
	 * 회원가입시 유저 정보를 저장하는 메서드
	 *
	 * @param requestDto {@link CreateUserLocalDto} 회원가입 정보
	 * @return {@link LoginResponseDto}
	 */
	LoginResponseDto createUser(final CreateUserLocalDto requestDto);

	/**
	 * OAuth2 소셜 로그인 회원가입 메서드
	 *
	 * @param requestDto {@link CreateUserOauth2Dto} 회원가입 정보
	 * @return {@link User}
	 */
	User createOAuth2User(final CreateUserOauth2Dto requestDto);

}
