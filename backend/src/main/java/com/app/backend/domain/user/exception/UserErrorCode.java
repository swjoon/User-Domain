package com.app.backend.domain.user.exception;

import com.app.backend.global.error.exception.DomainErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements DomainErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "회원을 찾지 못함");

    private final HttpStatus status;
    private final String code;
    private final String message;

}
