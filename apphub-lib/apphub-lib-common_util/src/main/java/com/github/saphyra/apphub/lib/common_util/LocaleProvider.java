package com.github.saphyra.apphub.lib.common_util;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
//TODO unit test
public class LocaleProvider {
    private final RequestContextProvider requestContextProvider;

    public Optional<String> getLocale() {
        return Optional.ofNullable(requestContextProvider.getCurrentHttpRequest().getHeader(Constants.LOCALE_HEADER));
    }
}
