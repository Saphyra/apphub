package com.github.saphyra.apphub.service.platform.main_gateway.service.locale;

import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.util.CookieUtil;
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
    private final SupportedLocales supportedLocales;

    Optional<String> getLocale(HttpServletRequest request) {
        return cookieUtil.getCookie(request, Constants.LOCALE_COOKIE)
            .filter(supportedLocales::isSupported);
    }
}
