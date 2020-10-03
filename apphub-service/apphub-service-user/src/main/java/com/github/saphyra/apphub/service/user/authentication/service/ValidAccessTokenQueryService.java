package com.github.saphyra.apphub.service.user.authentication.service;

import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.apphub.service.user.authentication.AuthenticationProperties;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessToken;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.util.OffsetDateTimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ValidAccessTokenQueryService {
    private final AccessTokenDao accessTokenDao;
    private final AuthenticationProperties authenticationProperties;
    private final OffsetDateTimeProvider offsetDateTimeProvider;
    private final UuidConverter uuidConverter;

    public Optional<AccessToken> findByAccessTokenId(UUID accessTokenId) {
        return accessTokenDao.findById(uuidConverter.convertDomain(accessTokenId))
            .filter(this::isValid);
    }

    private boolean isValid(AccessToken accessToken) {
        return accessToken.isPersistent() || accessToken.getLastAccess()
            .plusMinutes(authenticationProperties.getAccessTokenExpirationMinutes())
            .isAfter(offsetDateTimeProvider.getCurrentDate());
    }
}
