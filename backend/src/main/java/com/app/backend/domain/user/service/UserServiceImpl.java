package com.app.backend.domain.user.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.backend.domain.user.dto.request.CreateUserLocalDto;
import com.app.backend.domain.user.dto.request.CreateUserOauth2Dto;
import com.app.backend.domain.user.entity.User;
import com.app.backend.domain.user.exception.UserErrorCode;
import com.app.backend.domain.user.exception.UserException;
import com.app.backend.domain.user.mapper.UserMapper;
import com.app.backend.domain.user.repository.UserRepository;
import com.app.backend.global.config.security.constant.AuthConstant;
import com.app.backend.global.config.security.dto.response.LoginResponseDto;
import com.app.backend.global.config.security.info.domain.OAuth2ProviderInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final PasswordEncoder passwordEncoder;

	private final UserRepository userRepository;

	@Override
	@Transactional
	public LoginResponseDto createUser(final CreateUserLocalDto requestDto) {

		User user = CreateUserLocalDto
			.toEntity(requestDto, passwordEncoder.encode(requestDto.getPassword()));

		User createdUser = userRepository.createUser(user);

		return LoginResponseDto.from(createdUser.getId(), createdUser.getName());
	}

	@Override
	@Transactional
	public User createOAuth2User(final CreateUserOauth2Dto requestDto) {
		OAuth2ProviderInfo info = getProviderInfo(requestDto);

		validateProviderGoogle(requestDto, info);

		log.debug("Creating user with provider info: {}", info);

		User user = UserMapper.fromOAuth2Create(requestDto, info);

		return userRepository.createUser(user);
	}

	/**
	 * redis 에 저장된 oauth2 유저 정보 조회 메서드
	 *
	 * @param requestDto 회원가입 dto
	 * @return {@link OAuth2ProviderInfo}
	 */
	private OAuth2ProviderInfo getProviderInfo(final CreateUserOauth2Dto requestDto) {
		String uuid = requestDto.getUuid();

		Object info = redisTemplate.opsForValue().get(uuid);

		if (info == null) {
			throw new UserException(UserErrorCode.UUID_NOT_FOUND);
		}

		return (OAuth2ProviderInfo)info;
	}

	/**
	 * 구글 회원가입 전화번호 검증 메서드
	 *
	 * @param requestDto 회원가입 dto
	 * @param info oauth2 정보
	 */
	private void validateProviderGoogle(final CreateUserOauth2Dto requestDto, final OAuth2ProviderInfo info) {
		if (!info.getProvider().equals(AuthConstant.GOOGLE)) {
			return;
		}

		if (requestDto.getPhoneNumber() == null) {
			throw new UserException(UserErrorCode.PHONE_NUMBER_NOT_FOUND);
		}
	}
}
