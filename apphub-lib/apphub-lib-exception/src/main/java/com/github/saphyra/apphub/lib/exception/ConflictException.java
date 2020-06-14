package com.github.saphyra.apphub.lib.exception;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class ConflictException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.CONFLICT;

    public ConflictException(String logMessage) {
        super(STATUS, logMessage);
    }

    public ConflictException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public ConflictException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
