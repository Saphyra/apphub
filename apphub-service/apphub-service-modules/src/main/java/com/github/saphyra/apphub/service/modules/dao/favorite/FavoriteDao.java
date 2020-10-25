package com.github.saphyra.apphub.service.modules.dao.favorite;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class FavoriteDao extends AbstractDao<FavoriteEntity, Favorite, FavoriteEntityKey, FavoriteRepository> {
    private final UuidConverter uuidConverter;

    public FavoriteDao(FavoriteConverter converter, FavoriteRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public List<Favorite> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }
}
