package com.app.backend.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.backend.domain.user.entity.Provider;
import com.app.backend.domain.user.entity.User;

public interface UserJpaRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(final String username);

	Optional<User> findByProviderAndProviderId(final Provider provider, final String providerId);

}
