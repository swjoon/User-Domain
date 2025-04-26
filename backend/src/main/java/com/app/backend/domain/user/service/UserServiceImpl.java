package com.app.backend.domain.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.backend.domain.user.dto.request.CreateUserLocalDto;
import com.app.backend.domain.user.entity.User;
import com.app.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public long createUser(final CreateUserLocalDto createUserLocalDto) {

		User user = CreateUserLocalDto
			.toEntity(createUserLocalDto, passwordEncoder.encode(createUserLocalDto.getPassword()));

		return userRepository.createUser(user).getId();
	}
}
