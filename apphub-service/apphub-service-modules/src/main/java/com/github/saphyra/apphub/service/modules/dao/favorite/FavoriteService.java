package com.github.saphyra.apphub.service.modules.dao.favorite;

import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class FavoriteService {
    private final FavoriteDao favoriteDao;
    private final UuidConverter uuidConverter;

    public List<Favorite> getByUserId(UUID userId) {
        return favoriteDao.getByUserId(userId);
    }

    public Favorite getOrDefault(UUID userId, String module) {
        return favoriteDao.findById(FavoriteEntityKey.builder()
            .userId(uuidConverter.convertDomain(userId))
            .module(module)
            .build())
            .orElseGet(() -> Favorite.builder()
                .userId(userId)
                .module(module)
                .favorite(false)
                .build()
            );
    }

    public void save(Favorite favorite) {
        favoriteDao.save(favorite);
    }
}
