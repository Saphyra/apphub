package com.github.saphyra.apphub.lib.exception;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class FailedDependencyException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.FAILED_DEPENDENCY;

    public FailedDependencyException(String logMessage) {
        super(STATUS, logMessage);
    }

    public FailedDependencyException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public FailedDependencyException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
