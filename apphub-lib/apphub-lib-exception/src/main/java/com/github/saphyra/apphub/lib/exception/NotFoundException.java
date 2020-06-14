package com.github.saphyra.apphub.lib.exception;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class NotFoundException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    public NotFoundException(String logMessage) {
        super(STATUS, logMessage);
    }

    public NotFoundException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public NotFoundException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
