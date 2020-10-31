package com.github.saphyra.apphub.service.user.authentication.service;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccessTokenUpdateService {
    private final AccessTokenDao accessTokenDao;
    private final DateTimeUtil dateTimeUtil;

    public void updateLastAccess(UUID accessTokenId) {
        LocalDateTime currentDate = dateTimeUtil.getCurrentDate();
        accessTokenDao.updateLastAccess(accessTokenId, currentDate);
    }
}
