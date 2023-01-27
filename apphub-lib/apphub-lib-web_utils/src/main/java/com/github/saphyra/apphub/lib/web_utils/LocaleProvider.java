package com.github.saphyra.apphub.lib.web_utils;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;


@RequiredArgsConstructor
public class LocaleProvider {
    private final RequestContextProvider requestContextProvider;
    private final CommonConfigProperties commonConfigProperties;

    public String getOrDefault() {
        return requestContextProvider.getHttpServletRequestOptional()
            .flatMap(this::getLocale)
            .orElse(commonConfigProperties.getDefaultLocale());
    }

    public String getLocaleValidated() {
        return getLocale().orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.BAD_REQUEST, ErrorCode.LOCALE_NOT_FOUND, "Locale not found."));
    }

    public String getLocaleValidated(HttpServletRequest request) {
        return getLocale(request).orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.BAD_REQUEST, ErrorCode.LOCALE_NOT_FOUND, "Locale not found."));
    }

    public Optional<String> getLocale() {
        HttpServletRequest request = requestContextProvider.getCurrentHttpRequest();
        return getLocale(request);
    }

    public Optional<String> getLocale(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(Constants.LOCALE_HEADER))
            .filter(locale -> !isBlank(locale));
    }
}
