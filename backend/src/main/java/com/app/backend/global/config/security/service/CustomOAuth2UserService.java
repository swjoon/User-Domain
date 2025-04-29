package com.app.backend.global.config.security.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.backend.domain.user.entity.Provider;
import com.app.backend.domain.user.entity.User;
import com.app.backend.domain.user.exception.UserErrorCode;
import com.app.backend.domain.user.exception.UserException;
import com.app.backend.domain.user.repository.UserRepository;
import com.app.backend.global.config.security.info.CustomUserDetails;
import com.app.backend.global.config.security.info.OAuth2UserInfo;
import com.app.backend.global.config.security.info.domain.GoogleOAuth2UserInfo;
import com.app.backend.global.config.security.info.domain.KakaoOAuth2UserInfo;
import com.app.backend.global.config.security.info.domain.NaverOAuth2UserInfo;
import com.app.backend.global.util.UuidUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final UserRepository userRepository;

	@Override
	@Transactional
	public OAuth2User loadUser(final OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		OAuth2User oAuth2User = super.loadUser(userRequest);

		String registrationId = userRequest.getClientRegistration().getRegistrationId().toLowerCase();

		OAuth2UserInfo userInfo = switch (registrationId) {
			case "naver" -> new NaverOAuth2UserInfo(oAuth2User.getAttributes());
			case "google" -> new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
			case "kakao" -> new KakaoOAuth2UserInfo(oAuth2User.getAttributes());
			default -> throw new UserException(UserErrorCode.UNSUPPORTED_PROVIDER);
		};

		return process(userInfo);
	}

	private OAuth2User process(final OAuth2UserInfo userInfo) {
		log.debug("userInfo : {}", userInfo.toString());

		Provider provider = getProvider(userInfo.getProvider());
		String providerId = userInfo.getProviderId();

		Optional<User> opUser = userRepository.findByProviderAndProviderId(provider, providerId);

		if (opUser.isPresent()) {
			return new CustomUserDetails(opUser.get());
		}

		String uuid = UuidUtil.getUuid(LocalDate.now().toString(), 7);

		redisTemplate.opsForValue().set(uuid, userInfo, 5, TimeUnit.MINUTES);

		log.debug("추가 회원정보 uuid: {}, provider: {}", uuid, provider);

		Map<String, Object> attributes = new HashMap<>();

		attributes.put("uuid", uuid);
		attributes.put("provider", provider);
		attributes.put("needSignup", true);

		return new CustomUserDetails(null, attributes);
	}

	/**
	 * registrationId 값을 Enum 으로 변환하는 메서드
	 *
	 * @param registrationId OAuth2 공급자 (ex: "kakao", "naver")
	 * @return {@link Provider} Enum 값
	 * @throws UserException 지원하지 않는 Provider 일 경우 예외
	 * @implSpec Enum 으로 매핑
	 */
	private Provider getProvider(String registrationId) {
		try {
			return Provider.valueOf(registrationId.toUpperCase());
		} catch (Exception e) {
			throw new UserException(UserErrorCode.UNSUPPORTED_PROVIDER);
		}
	}
}

