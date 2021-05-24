package com.github.saphyra.apphub.lib.web_utils;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;


@RequiredArgsConstructor
public class LocaleProvider {
    private final RequestContextProvider requestContextProvider;
    private final CommonConfigProperties commonConfigProperties;

    public String getOrDefault() {
        return requestContextProvider.getHttpServletRequestOptional()
            .map(this::getLocaleValidated)
            .orElse(commonConfigProperties.getDefaultLocale());
    }

    public String getLocaleValidated() {
        return getLocale().orElseThrow(() -> new BadRequestException(new ErrorMessage(ErrorCode.LOCALE_NOT_FOUND.name()), "Locale not found."));
    }

    public String getLocaleValidated(HttpServletRequest request) {
        return getLocale(request).orElseThrow(() -> new BadRequestException(new ErrorMessage(ErrorCode.LOCALE_NOT_FOUND.name()), "Locale not found."));
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
