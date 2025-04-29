package com.app.backend.global.config.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.app.backend.domain.user.entity.User;
import com.app.backend.domain.user.exception.UserErrorCode;
import com.app.backend.domain.user.exception.UserException;
import com.app.backend.domain.user.repository.UserRepository;
import com.app.backend.global.config.security.info.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = findUserByUsername(username);

		return new CustomUserDetails(user);
	}

	/**
	 * 유저 정보 조회 메서드
	 *
	 * @param username 유저 ID
	 * @return {@link User}
	 */
	private User findUserByUsername(String username) {

		return userRepository.findByUsername(username)
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
	}
}
