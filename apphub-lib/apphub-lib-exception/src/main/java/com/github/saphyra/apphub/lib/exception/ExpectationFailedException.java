package com.github.saphyra.apphub.lib.exception;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class ExpectationFailedException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.EXPECTATION_FAILED;

    public ExpectationFailedException(String logMessage) {
        super(STATUS, logMessage);
    }

    public ExpectationFailedException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public ExpectationFailedException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
