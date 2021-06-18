package com.github.saphyra.apphub.test.common.rest_assured;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorResponseValidator {
    public static void verifyInvalidParam(Response response, String field, String value) {
        ErrorResponse errorResponse = verifyBadRequest(response, ErrorCode.INVALID_PARAM);
        verifyParam(errorResponse, field, value);
    }

    public static ErrorResponse verifyBadRequest(Response response, ErrorCode errorCode) {
        return verifyErrorResponse(response, 400, errorCode);
    }

    public static ErrorResponse verifyErrorResponse(Response response, int status, ErrorCode errorCode) {
        assertThat(response.getStatusCode()).isEqualTo(status);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(errorCode);
        assertThat(errorResponse.getLocalizedMessage()).isNotBlank();
        return errorResponse;
    }

    private static void verifyParam(ErrorResponse errorResponse, String field, String value) {
        assertThat(errorResponse.getParams().get(field)).isEqualTo(value);
    }
}
