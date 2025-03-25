package com.app.backend.domain.user.exception;

import com.app.backend.global.error.exception.DomainErrorCode;
import com.app.backend.global.error.exception.DomainException;

public class UserException extends DomainException {

    public UserException(DomainErrorCode domainErrorCode) {
        super(domainErrorCode);
    }
}
