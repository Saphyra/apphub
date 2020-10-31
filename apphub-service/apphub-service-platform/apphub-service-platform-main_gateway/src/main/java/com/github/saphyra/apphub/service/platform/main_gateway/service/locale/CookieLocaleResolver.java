package com.github.saphyra.apphub.service.platform.main_gateway.service.locale;

import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.common_util.CookieUtil;
import com.github.saphyra.apphub.lib.config.CommonConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Component
class CookieLocaleResolver {
    private final CookieUtil cookieUtil;
    private final CommonConfigProperties commonConfigProperties;

    Optional<String> getLocale(HttpServletRequest request) {
        return cookieUtil.getCookie(request, Constants.LOCALE_COOKIE)
            .filter(locale -> commonConfigProperties.getSupportedLocales().contains(locale));
    }
}
