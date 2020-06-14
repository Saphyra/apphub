package com.github.saphyra.apphub.lib.exception;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import org.springframework.http.HttpStatus;

public class UpgradeRequiredException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.UPGRADE_REQUIRED;

    public UpgradeRequiredException(String logMessage) {
        super(STATUS, logMessage);
    }

    public UpgradeRequiredException(ErrorMessage errorMessage, String logMessage) {
        super(STATUS, errorMessage, logMessage);
    }

    public UpgradeRequiredException(String errorCode, String logMessage) {
        super(STATUS, errorCode, logMessage);
    }
}
