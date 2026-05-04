package com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository {
    void save(RefreshTokenEntity refreshToken);

    void delete(String userId, String refreshTokenId);

    Optional<RefreshTokenEntity> findById(String userId, String refreshTokenId);

    List<RefreshTokenEntity> getByUserId(String userId);

    void delete(List<RefreshTokenEntity> entities);
}
