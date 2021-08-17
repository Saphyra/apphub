package com.github.saphyra.apphub.service.platform.main_gateway.service.locale;

import com.github.saphyra.apphub.api.user.client.UserDataApiClient;
import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.common_util.AbstractCache;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.service.platform.main_gateway.service.AccessTokenQueryService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.github.saphyra.apphub.lib.common_domain.Constants.ACCESS_TOKEN_COOKIE;

@RequiredArgsConstructor
@Slf4j
@Component
public class UserSettingLocaleResolver {
    private final Cache<UUID, Optional<String>> cache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();
    private final AbstractCache<UUID, String> localeCache = new AbstractCache<UUID, String>(cache) {
        @Override
        protected Optional<String> load(UUID userId) {
            String language = userDataApi.getLanguage(userId, commonConfigProperties.getDefaultLocale());
            log.debug("Language {} cached for userId {}", language, userId);
            return Optional.of(language);
        }
    };

    private final AccessTokenQueryService accessTokenQueryService;
    private final CommonConfigProperties commonConfigProperties;
    private final UserDataApiClient userDataApi;

    Optional<String> getLocale(MultiValueMap<String, HttpCookie> cookies) {
        Optional<UUID> userIdOptional = extractUserId(cookies);
        if (userIdOptional.isPresent()) {
            try {
                return localeCache.get(userIdOptional.get());
            } catch (Exception e) {
                log.warn("Error occurred during userLanguage query", e);
            }
        }
        return Optional.empty();
    }

    private Optional<UUID> extractUserId(MultiValueMap<String, HttpCookie> cookies) {
        return Optional.ofNullable(cookies.getFirst(ACCESS_TOKEN_COOKIE))
            .map(HttpCookie::getValue)
            .flatMap(accessTokenQueryService::getAccessToken)
            .map(InternalAccessTokenResponse::getUserId);
    }

    public void invalidate(MultiValueMap<String, HttpCookie> cookies) {
        extractUserId(cookies)
            .ifPresent(userId -> {
                log.debug("Invalidating cached locale for userId {}", userId);
                localeCache.invalidate(userId);
            });
    }
}
