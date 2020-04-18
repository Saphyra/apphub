package com.github.saphyra.apphub.lib.error_handler.exception;

import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class BadRequestException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    public BadRequestException(String logMessage) {
        super(STATUS, logMessage);
    }

    public BadRequestException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public BadRequestException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
