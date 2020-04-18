package com.github.saphyra.apphub.lib.error_handler.exception;

import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class PreconditionFailedException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.PRECONDITION_FAILED;

    public PreconditionFailedException(String logMessage) {
        super(STATUS, logMessage);
    }

    public PreconditionFailedException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public PreconditionFailedException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
