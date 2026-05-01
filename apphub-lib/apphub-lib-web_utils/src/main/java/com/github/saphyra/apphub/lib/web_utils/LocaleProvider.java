package com.github.saphyra.apphub.lib.web_utils;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Deprecated(forRemoval = true) //TODO delete
@RequiredArgsConstructor
public class LocaleProvider {
    private final RequestContextProvider requestContextProvider;
    private final CommonConfigProperties commonConfigProperties;

    public String getOrDefault() {
        return requestContextProvider.getHttpServletRequestOptional()
            .flatMap(this::getLocale)
            .orElse(commonConfigProperties.getDefaultLocale());
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
