package com.app.backend.domain.user.exception;

import com.app.backend.global.error.exception.DomainErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements DomainErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "일치하는 회원을 찾지 못했습니다."),
    UNSUPPORTED_PROVIDER(HttpStatus.BAD_REQUEST, "U002", "지원하지 않는 제공자 입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

}
