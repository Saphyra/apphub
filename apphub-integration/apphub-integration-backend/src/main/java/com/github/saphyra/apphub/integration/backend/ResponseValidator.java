package com.github.saphyra.apphub.integration.backend;

import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import io.restassured.response.Response;

import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.LIST_ITEM_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

public class ResponseValidator {
    public static void verifyInvalidParam(Language language, Response response, String field, String value) {
        ErrorResponse errorResponse = verifyBadRequest(language, response, ErrorCode.INVALID_PARAM);
        verifyParam(errorResponse, field, value);
    }

    private static void verifyParam(ErrorResponse errorResponse, String field, String value) {
        assertThat(errorResponse.getParams().get(field)).isEqualTo(value);
    }

    public static ErrorResponse verifyBadRequest(Language language, Response response, ErrorCode errorCode) {
        return verifyErrorResponse(language, response, 400, errorCode);
    }

    public static ErrorResponse verifyErrorResponse(Language language, Response response, int status, ErrorCode errorCode) {
        assertThat(response.getStatusCode()).isEqualTo(status);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(errorCode.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.fromErrorCode(errorCode)));
        return errorResponse;
    }

    public static void verifyErrorResponse(Language language, Response response, int status, ErrorCode errorCode, String field, String value) {
        ErrorResponse errorResponse = verifyErrorResponse(language, response, status, errorCode);
        verifyParam(errorResponse, field, value);
    }

    public static void verifyListItemNotFound(Language language, Response edit_columnHeadNotFoundResponse) {
        assertThat(edit_columnHeadNotFoundResponse.getStatusCode()).isEqualTo(404);

        ErrorResponse errorResponse = edit_columnHeadNotFoundResponse.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LIST_ITEM_NOT_FOUND));
    }

    public static void verifyForbiddenOperation(Language language, Response response) {
        assertThat(response.getStatusCode()).isEqualTo(403);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_OPERATION.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.FORBIDDEN_OPERATION));
    }

    public static void verifyNotTranslatedNotFound(Response chatRoomNotFoundResponse, int status) {
        assertThat(chatRoomNotFoundResponse.getStatusCode()).isEqualTo(status);
        ErrorResponse errorResponse = chatRoomNotFoundResponse.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.GENERAL_ERROR.name());
    }
}
