package com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Profile("test")
public class InMemoryRefreshTokenRepository implements RefreshTokenRepository {
    private final List<RefreshTokenEntity> repository = new CopyOnWriteArrayList<>();

    @Override
    public void save(RefreshTokenEntity refreshToken) {
        repository.add(refreshToken);
    }

    @Override
    public void delete(String userId, String refreshTokenId) {
        repository.removeIf(entity -> entity.getUserId().equals(userId) && entity.getRefreshTokenId().equals(refreshTokenId));
    }

    @Override
    public Optional<RefreshTokenEntity> findById(String userId, String refreshTokenId) {
        return repository.stream()
            .filter(entity -> entity.getUserId().equals(userId))
            .filter(entity -> entity.getRefreshTokenId().equals(refreshTokenId))
            .findFirst();
    }

    @Override
    public List<RefreshTokenEntity> getByUserId(String userId) {
        return repository.stream()
            .filter(entity -> entity.getUserId().equals(userId))
            .toList();
    }

    @Override
    public void delete(List<RefreshTokenEntity> entities) {
        repository.removeAll(entities);
    }
}
