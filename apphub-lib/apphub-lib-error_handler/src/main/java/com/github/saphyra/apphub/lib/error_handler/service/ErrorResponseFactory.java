package com.github.saphyra.apphub.lib.error_handler.service;

import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import com.github.saphyra.apphub.lib.config.CommonConfigProperties;
import com.github.saphyra.apphub.lib.error_handler.domain.ErrorResponse;
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
    private static final String LOCALE_NOT_FOUND_ERROR_CODE = "LOCALE_NOT_FOUND";

    private final CommonConfigProperties commonConfigProperties;
    private final LocaleProvider localeProvider;
    private final LocalizedMessageProvider localizedMessageProvider;

    public ErrorResponse create(HttpStatus httpStatus, String errorCode, Map<String, String> params) {
        return localeProvider.getLocale()
            .map(locale -> createErrorResponse(locale, httpStatus, errorCode, params))
            .orElseGet(this::createLocaleNotFoundErrorResponse);
    }

    public ErrorResponse createErrorResponse(String locale, HttpStatus httpStatus, String errorCode, Map<String, String> params) {
        String localizedMessage = localizedMessageProvider.getLocalizedMessage(locale, errorCode, params);

        return ErrorResponse.builder()
            .httpStatus(httpStatus.value())
            .errorCode(errorCode)
            .localizedMessage(localizedMessage)
            .params(params)
            .build();
    }

    private ErrorResponse createLocaleNotFoundErrorResponse() {
        log.warn("Locale not found.");
        String localizedMessage = localizedMessageProvider.getLocalizedMessage(commonConfigProperties.getDefaultLocale(), LOCALE_NOT_FOUND_ERROR_CODE, new HashMap<>());

        return ErrorResponse.builder()
            .httpStatus(HttpStatus.BAD_REQUEST.value())
            .errorCode(LOCALE_NOT_FOUND_ERROR_CODE)
            .localizedMessage(localizedMessage)
            .params(new HashMap<>())
            .build();
    }
}