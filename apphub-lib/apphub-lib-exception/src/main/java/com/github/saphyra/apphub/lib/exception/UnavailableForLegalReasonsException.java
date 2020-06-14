package com.github.saphyra.apphub.lib.exception;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class UnavailableForLegalReasonsException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS;

    public UnavailableForLegalReasonsException(String logMessage) {
        super(STATUS, logMessage);
    }

    public UnavailableForLegalReasonsException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public UnavailableForLegalReasonsException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
