package com.github.saphyra.apphub.service.user.authentication.service;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.user.authentication.AuthenticationProperties;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccessTokenCleanupService {
    private final AccessTokenDao accessTokenDao;
    private final AuthenticationProperties authenticationProperties;
    private final DateTimeUtil dateTimeUtil;

    public void deleteExpiredAccessTokens() {
        LocalDateTime currentDate = dateTimeUtil.getCurrentDate();

        LocalDateTime persistentExpirationDate = currentDate.minusDays(authenticationProperties.getAccessTokenCookieExpirationDays());
        LocalDateTime nonPersistentExpirationDate = currentDate.minusMinutes(authenticationProperties.getAccessTokenExpirationMinutes());

        accessTokenDao.deleteByPersistentAndLastAccessBefore(true, persistentExpirationDate);
        accessTokenDao.deleteByPersistentAndLastAccessBefore(false, nonPersistentExpirationDate);
    }
}
