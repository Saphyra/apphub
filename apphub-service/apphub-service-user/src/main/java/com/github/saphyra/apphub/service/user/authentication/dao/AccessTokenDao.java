package com.github.saphyra.apphub.service.user.authentication.dao;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class AccessTokenDao extends AbstractDao<AccessTokenEntity, AccessToken, String, AccessTokenRepository> {
    private final UuidConverter uuidConverter;

    public AccessTokenDao(AccessTokenConverter converter, AccessTokenRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByPersistentAndLastAccessBefore(boolean persistent, LocalDateTime expiration) {
        repository.deleteByPersistentAndLastAccessBefore(persistent, expiration);
    }

    public void updateLastAccess(UUID accessTokenId, LocalDateTime currentDate) {
        repository.updateLastAccess(
            uuidConverter.convertDomain(accessTokenId),
            currentDate
        );
    }

    public void deleteByAccessTokenIdAndUserId(UUID accessTokenId, UUID userId) {
        repository.deleteByAccessTokenIdAndUserId(
            uuidConverter.convertDomain(accessTokenId),
            uuidConverter.convertDomain(userId)
        );
    }

    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }
}
