package com.github.saphyra.apphub.lib.exception;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.Map;

//Exception to be used for serious errors, and should be reported to the database
public class ReportedException extends RestException {
    ReportedException(String message) {
        super(message);
    }

    ReportedException(HttpStatus status, ErrorCode errorCode, String message) {
        super(status, errorCode, message);
    }

    ReportedException(HttpStatus status, ErrorCode errorCode, Map<String, String> params, String message) {
        super(status, errorCode, params, message);
    }

    ReportedException(HttpStatus status, String message) {
        super(status, message);
    }
}
