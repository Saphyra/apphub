package com.github.saphyra.apphub.lib.error_handler.exception;

import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class NotImplementedException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.NOT_IMPLEMENTED;

    public NotImplementedException(String logMessage) {
        super(STATUS, logMessage);
    }

    public NotImplementedException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public NotImplementedException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
