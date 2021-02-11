package com.github.saphyra.apphub.service.skyxplore.game.common;

import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
            return commonConfigProperties.getDefaultLocale();
        }
    }
}
