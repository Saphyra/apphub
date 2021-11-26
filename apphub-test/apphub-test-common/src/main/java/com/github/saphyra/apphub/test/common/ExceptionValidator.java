package com.github.saphyra.apphub.test.common;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.LoggedException;
import com.github.saphyra.apphub.lib.exception.NotLoggedException;
import com.github.saphyra.apphub.lib.exception.ReportedException;
import com.github.saphyra.apphub.lib.exception.RestException;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class ExceptionValidator {
    public static void validateLoggedException(Throwable ex, HttpStatus status, ErrorCode errorCode) {
        assertThat(ex).isInstanceOf(LoggedException.class);
        validateRestException((RestException) ex, status, errorCode);
    }

    public static void validateReportedException(Throwable ex, HttpStatus status, ErrorCode errorCode) {
        assertThat(ex).isInstanceOf(ReportedException.class);
        validateRestException((RestException) ex, status, errorCode);
    }

    public static void validateNotLoggedException(Throwable ex, HttpStatus status, ErrorCode errorCode) {
        assertThat(ex).isInstanceOf(NotLoggedException.class);
        validateRestException((RestException) ex, status, errorCode);
    }

    public static void validateNotLoggedException(Throwable ex, HttpStatus status, ErrorCode errorCode, String field, String value) {
        assertThat(ex).isInstanceOf(NotLoggedException.class);
        validateRestException((RestException) ex, status, errorCode, field, value);
    }

    private static void validateRestException(RestException ex, HttpStatus status, ErrorCode errorCode, String field, String value) {
        assertThat(ex.getErrorMessage().getParams()).containsEntry(field, value);
        validateRestException(ex, status, errorCode);
    }

    private static void validateRestException(RestException ex, HttpStatus status, ErrorCode errorCode) {
        assertThat(ex.getResponseStatus()).isEqualTo(status);
        assertThat(ex.getErrorMessage().getErrorCode()).isEqualTo(errorCode);
    }

    public static void validateInvalidParam(Throwable ex, String field, String value) {
        validateNotLoggedException(ex, HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAM, field, value);
    }

    public static void validateInvalidType(Throwable ex) {
        validateNotLoggedException(ex, HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.INVALID_TYPE);
    }

    public static void validateNotLoggedException(Throwable ex, HttpStatus status) {
        assertThat(ex).isInstanceOf(NotLoggedException.class);
        assertThat(((RestException) ex).getResponseStatus()).isEqualTo(status);
    }

    public static void validateForbiddenOperation(Throwable ex) {
        validateNotLoggedException(ex, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION);
    }
}
