package com.app.backend.domain.user.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.app.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

	private final UserJpaRepository userJpaRepository;

	@Override
	public User createUser(final User user) {

		return userJpaRepository.save(user);
	}

	@Override
	public Optional<User> findByUserId(final Long userId) {

		return userJpaRepository.findById(userId);
	}

	@Override
	public Optional<User> findByUsername(final String username) {

		return userJpaRepository.findByUsername(username);
	}
}
