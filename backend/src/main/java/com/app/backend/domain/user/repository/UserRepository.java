package com.app.backend.domain.user.repository;

import java.util.Optional;

import com.app.backend.domain.user.entity.Provider;
import com.app.backend.domain.user.entity.User;

public interface UserRepository {

	/**
	 * 유저 회원가입 및 정보 저장 메서드
	 *
	 * @param user {@link User}
	 */
	User createUser(final User user);

	/**
	 * 유저 고유 ID와 일치하는 정보 조회 메서드
	 *
	 * @param userId 유저 ID
	 * @return {@link User}
	 */
	Optional<User> findByUserId(final Long userId);

	/**
	 * 유저 로그인 ID와 일치하는 정보 조회 메서드
	 *
	 * @param username 로그인 ID
	 * @return {@link User}
	 */
	Optional<User> findByUsername(final String username);

	/**
	 * OAuth2 식별자와 ID로 일치하는 정보 조회 메서드
	 *
	 * @param provider 식별자
	 * @param providerId 소설 제공 ID
	 * @return {@link User}
	 */
	Optional<User> findByProviderAndProviderId(final Provider provider, final String providerId);
}
