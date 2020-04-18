package com.github.saphyra.apphub.lib.error_handler.exception;

import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class LockedException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.LOCKED;

    public LockedException(String logMessage) {
        super(STATUS, logMessage);
    }

    public LockedException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public LockedException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
