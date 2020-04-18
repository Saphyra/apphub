package com.github.saphyra.apphub.lib.error_handler.exception;

import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class UnprocessableEntityException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.UNPROCESSABLE_ENTITY;

    public UnprocessableEntityException(String logMessage) {
        super(STATUS, logMessage);
    }

    public UnprocessableEntityException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public UnprocessableEntityException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
