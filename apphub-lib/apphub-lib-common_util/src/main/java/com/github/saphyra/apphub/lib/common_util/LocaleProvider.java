package com.github.saphyra.apphub.lib.common_util;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static org.apache.logging.log4j.util.Strings.isBlank;

@RequiredArgsConstructor
public class LocaleProvider {
    private final RequestContextProvider requestContextProvider;

    public Optional<String> getLocale() {
        return Optional.ofNullable(requestContextProvider.getCurrentHttpRequest().getHeader(Constants.LOCALE_HEADER))
            .filter(locale -> !isBlank(locale));
    }
}
