package com.github.saphyra.apphub.lib.error_handler.exception;

import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class GoneException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.GONE;

    public GoneException(String logMessage) {
        super(STATUS, logMessage);
    }

    public GoneException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public GoneException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
