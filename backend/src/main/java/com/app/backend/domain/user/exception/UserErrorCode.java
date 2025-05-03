package com.app.backend.domain.user.exception;

import org.springframework.http.HttpStatus;

import com.app.backend.global.error.exception.DomainErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements DomainErrorCode {

	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "일치하는 회원을 찾지 못했습니다."),
	UNSUPPORTED_PROVIDER(HttpStatus.BAD_REQUEST, "U002", "지원하지 않는 제공자 입니다."),
	UUID_NOT_FOUND(HttpStatus.NOT_FOUND, "U003", "UUID 와 일치하는 정보가 없습니다."),
	PHONE_NUMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "U004", "핸드폰 번호는 필수 항목입니다."),
	OAUTH2_SIGNUP_FAILED(HttpStatus.BAD_REQUEST, "U005", "소셜 회원가입에 실패했습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;

}
