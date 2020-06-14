package com.github.saphyra.apphub.lib.exception;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.FORBIDDEN;

    public ForbiddenException(String logMessage) {
        super(STATUS, logMessage);
    }

    public ForbiddenException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public ForbiddenException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
