package com.github.saphyra.apphub.lib.error_handler.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import com.github.saphyra.apphub.lib.config.CommonConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Component
public class ErrorResponseFactory {
    private final CommonConfigProperties commonConfigProperties;
    private final LocaleProvider localeProvider;
    private final LocalizedMessageProvider localizedMessageProvider;

    public ErrorResponseWrapper create(HttpStatus httpStatus, String errorCode, Map<String, String> params) {
        return localeProvider.getLocale()
            .map(locale -> createErrorResponse(locale, httpStatus, errorCode, params))
            .orElseGet(this::createLocaleNotFoundErrorResponse);
    }

    public ErrorResponseWrapper createErrorResponse(String locale, HttpStatus status, String errorCode) {
        return createErrorResponse(locale, status, errorCode, new HashMap<>());
    }

    public ErrorResponseWrapper createErrorResponse(String locale, HttpStatus httpStatus, String errorCode, Map<String, String> params) {
        String localizedMessage = localizedMessageProvider.getLocalizedMessage(locale, errorCode, params);

        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorCode(errorCode)
            .localizedMessage(localizedMessage)
            .params(params)
            .build();
        return new ErrorResponseWrapper(errorResponse, httpStatus);
    }

    private ErrorResponseWrapper createLocaleNotFoundErrorResponse() {
        log.warn("Locale not found.");
        String localizedMessage = localizedMessageProvider.getLocalizedMessage(commonConfigProperties.getDefaultLocale(), ErrorCode.LOCALE_NOT_FOUND.name(), new HashMap<>());

        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorCode(ErrorCode.LOCALE_NOT_FOUND.name())
            .localizedMessage(localizedMessage)
            .params(new HashMap<>())
            .build();
        return new ErrorResponseWrapper(errorResponse, HttpStatus.BAD_REQUEST);
    }
}