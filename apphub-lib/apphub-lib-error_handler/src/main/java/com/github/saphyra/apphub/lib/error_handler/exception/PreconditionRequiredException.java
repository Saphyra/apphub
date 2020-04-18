package com.github.saphyra.apphub.lib.error_handler.exception;

import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class PreconditionRequiredException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.PRECONDITION_REQUIRED;

    public PreconditionRequiredException(String logMessage) {
        super(STATUS, logMessage);
    }

    public PreconditionRequiredException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public PreconditionRequiredException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
