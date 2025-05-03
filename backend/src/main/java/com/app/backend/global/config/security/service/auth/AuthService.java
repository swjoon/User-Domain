package com.app.backend.global.config.security.service.auth;

import java.io.IOException;

import com.app.backend.domain.user.dto.request.CreateUserOauth2Dto;

import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

	/**
	 * OAuth2 회원가입 진행 후 성공시 로그인 상태로 반환하는 메서드
	 *
	 * @param requestDto OAuth2 회원가입 추가 정보
	 * @param response API 응답
	 */
	void oAuth2SignUpAndSignIn(final CreateUserOauth2Dto requestDto, final HttpServletResponse response);

}
