package com.github.saphyra.apphub.lib.error_handler.service.translation;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import jakarta.servlet.http.HttpServletRequest;
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

    public ErrorResponseWrapper create(HttpStatus httpStatus, ErrorCode errorCode) {
        return create(httpStatus, errorCode, new HashMap<>());
    }

    public ErrorResponseWrapper create(HttpStatus httpStatus, ErrorCode errorCode, Map<String, String> params) {
        return localeProvider.getLocale()
            .map(locale -> create(locale, httpStatus, errorCode, params))
            .orElseGet(this::createLocaleNotFoundErrorResponse);
    }

    public ErrorResponseWrapper create(HttpServletRequest request, HttpStatus httpStatus, ErrorCode errorCode) {
        return localeProvider.getLocale(request)
            .map(locale -> create(locale, httpStatus, errorCode, new HashMap<>()))
            .orElseGet(this::createLocaleNotFoundErrorResponse);
    }

    public ErrorResponseWrapper create(String locale, HttpStatus status, ErrorCode errorCode) {
        return create(locale, status, errorCode, new HashMap<>());
    }

    public ErrorResponseWrapper create(String locale, HttpStatus httpStatus, ErrorCode errorCode, Map<String, String> params) {
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
        String localizedMessage = localizedMessageProvider.getLocalizedMessage(commonConfigProperties.getDefaultLocale(), ErrorCode.LOCALE_NOT_FOUND, new HashMap<>());

        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorCode(ErrorCode.LOCALE_NOT_FOUND)
            .localizedMessage(localizedMessage)
            .params(new HashMap<>())
            .build();
        return new ErrorResponseWrapper(errorResponse, HttpStatus.BAD_REQUEST);
    }
}