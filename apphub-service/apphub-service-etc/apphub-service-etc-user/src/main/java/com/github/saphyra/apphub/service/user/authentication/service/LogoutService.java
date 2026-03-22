package com.github.saphyra.apphub.service.user.authentication.service;

import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogoutService {
    private final AccessTokenDao accessTokenDao;

    public void logout(UUID accessTokenId, UUID userId) {
        accessTokenDao.deleteByAccessTokenIdAndUserId(accessTokenId, userId);
    }
}
