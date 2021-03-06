package com.github.saphyra.apphub.lib.exception;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class InsufficientStorageException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.INSUFFICIENT_STORAGE;

    public InsufficientStorageException(String logMessage) {
        super(STATUS, logMessage);
    }

    public InsufficientStorageException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public InsufficientStorageException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
