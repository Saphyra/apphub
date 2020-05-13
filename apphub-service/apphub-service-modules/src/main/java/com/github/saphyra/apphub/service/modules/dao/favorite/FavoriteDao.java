package com.github.saphyra.apphub.service.modules.dao.favorite;

import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.dao.AbstractDao;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
//TODO unit test
class FavoriteDao extends AbstractDao<FavoriteEntity, Favorite, FavoriteEntityKey, FavoriteRepository> {
    private final UuidConverter uuidConverter;

    public FavoriteDao(FavoriteConverter converter, FavoriteRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public List<Favorite> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    //TODO unit test
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }
}
