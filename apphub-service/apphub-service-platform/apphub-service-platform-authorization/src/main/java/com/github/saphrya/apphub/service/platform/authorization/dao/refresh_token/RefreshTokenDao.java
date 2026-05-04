package com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class RefreshTokenDao {
    private final RefreshTokenRepository repository;
    private final RefreshTokenConverter converter;
    private final UuidConverter uuidConverter;

    public void save(RefreshToken refreshToken) {
        repository.save(converter.convertDomain(refreshToken));
    }

    public void delete(UUID userId, UUID refreshTokenId) {
        repository.delete(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(refreshTokenId));
    }

    public Optional<RefreshToken> findByUserIdAndRefreshTokenId(UUID userId, UUID refreshTokenId) {
        return converter.convertEntity(repository.findById(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(refreshTokenId)));
    }

    public List<UUID> deleteByUserId(UUID userId) {
        List<RefreshTokenEntity> toDelete = repository.getByUserId(uuidConverter.convertDomain(userId));

        Lists.partition(toDelete, Constants.DYNAMO_DB_DELETE_MAX_BATCH_SIZE)
                .forEach(repository::delete);

        return toDelete.stream()
            .map(RefreshTokenEntity::getRefreshTokenId)
            .map(uuidConverter::convertEntity)
            .toList();
    }

    public List<RefreshToken> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }
}
