package com.github.saphyra.apphub.lib.exception;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class NotAcceptableException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.NOT_ACCEPTABLE;

    public NotAcceptableException(String logMessage) {
        super(STATUS, logMessage);
    }

    public NotAcceptableException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public NotAcceptableException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
