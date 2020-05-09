package com.github.saphyra.apphub.service.user.authentication.service;

import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.util.OffsetDateTimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccessTokenUpdateService {
    private final AccessTokenDao accessTokenDao;
    private final OffsetDateTimeProvider offsetDateTimeProvider;

    public void updateLastAccess(UUID accessTokenId) {
        OffsetDateTime currentDate = offsetDateTimeProvider.getCurrentDate();
        accessTokenDao.updateLastAccess(accessTokenId, currentDate);
    }
}
