package com.app.backend.domain.user.service;

import com.app.backend.domain.user.dto.request.CreateUserLocalDto;

public interface UserService {

	/**
	 * 회원가입시 유저 정보를 저장하는 메서드
	 *
	 * @param {@link CreateUserLocalDto}
	 * @return 유저 고유 ID
	 */
	long createUser(final CreateUserLocalDto requestDto);

}
