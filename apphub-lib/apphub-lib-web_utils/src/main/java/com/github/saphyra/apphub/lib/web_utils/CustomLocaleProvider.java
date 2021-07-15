package com.github.saphyra.apphub.lib.web_utils;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomLocaleProvider {
    private final CommonConfigProperties commonConfigProperties;
    private final LocaleProvider localeProvider;

    public String getLocale() {
        try {
            return localeProvider.getLocale()
                .orElseGet(commonConfigProperties::getDefaultLocale);
        } catch (Exception e) {
            log.debug("Locale could not be read from delegate. Returning default locale...", e);
            return commonConfigProperties.getDefaultLocale();
        }
    }
}
