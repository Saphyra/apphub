package com.github.saphyra.apphub.service.user.authentication.service;

import com.github.saphyra.apphub.lib.common_util.OffsetDateTimeProvider;
import com.github.saphyra.apphub.service.user.authentication.AuthenticationProperties;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccessTokenCleanupService {
    private final AccessTokenDao accessTokenDao;
    private final AuthenticationProperties authenticationProperties;
    private final OffsetDateTimeProvider offsetDateTimeProvider;

    public void deleteExpiredAccessTokens() {
        OffsetDateTime currentDate = offsetDateTimeProvider.getCurrentDate();

        OffsetDateTime persistentExpirationDate = currentDate.minusDays(authenticationProperties.getAccessTokenCookieExpirationDays());
        OffsetDateTime nonPersistentExpirationDate = currentDate.minusMinutes(authenticationProperties.getAccessTokenExpirationMinutes());

        accessTokenDao.deleteByPersistentAndLastAccessBefore(true, persistentExpirationDate);
        accessTokenDao.deleteByPersistentAndLastAccessBefore(false, nonPersistentExpirationDate);
    }
}
