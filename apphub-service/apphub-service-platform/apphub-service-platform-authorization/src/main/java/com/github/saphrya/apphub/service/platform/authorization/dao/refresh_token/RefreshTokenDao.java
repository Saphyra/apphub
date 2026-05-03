package com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;

@Component
//TODO unit test
public class RefreshTokenDao {
    private final List<RefreshToken> repository = new Vector<>(); //TODO use DynamoDB

    public void save(RefreshToken refreshToken) {
        repository.add(refreshToken);
    }

    public void delete(UUID userId, UUID refreshTokenId) {
        repository.removeIf(refreshToken -> refreshToken.getUserId().equals(userId) && refreshToken.getRefreshTokenId().equals(refreshTokenId));
    }

    public Optional<RefreshToken> findByUserIdAndRefreshTokenId(UUID userId, UUID refreshTokenId) {
        return repository.stream()
            .filter(refreshToken -> refreshToken.getUserId().equals(userId))
            .filter(refreshToken -> refreshToken.getRefreshTokenId().equals(refreshTokenId))
            .findFirst();
    }

    public List<UUID> deleteByUserId(UUID userId) {
        List<RefreshToken> toDelete = getByUserId(userId);

        repository.removeAll(toDelete);

        return toDelete.stream()
            .map(RefreshToken::getRefreshTokenId)
            .toList();
    }

    public List<RefreshToken> getByUserId(UUID userId) {
        return repository.stream()
            .filter(refreshToken -> refreshToken.getUserId().equals(userId))
            .toList();
    }
}
