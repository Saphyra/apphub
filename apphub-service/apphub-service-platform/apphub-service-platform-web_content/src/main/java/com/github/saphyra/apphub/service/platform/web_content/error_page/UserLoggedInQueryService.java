package com.github.saphyra.apphub.service.platform.web_content.error_page;

import com.github.saphyra.apphub.api.user.client.UserAuthenticationClient;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class UserLoggedInQueryService {
    private final UserAuthenticationClient authenticationClient;
    private final LocaleProvider localeProvider;

    boolean isUserLoggedIn(UUID accessTokenId) {
        try {
            if (isNull(accessTokenId)) {
                return false;
            }

            authenticationClient.getAccessTokenById(accessTokenId, localeProvider.getLocaleValidated());
            return true;
        } catch (Exception e) {
            log.debug("Failed querying accessToken", e);
            return false;
        }
    }
}
