package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.reserved_storage;

import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ReservedStorageDao extends AbstractDao<ReservedStorageEntity, ReservedStorageModel, String, ReservedStorageRepository> {
    private final UuidConverter uuidConverter;

    public ReservedStorageDao(ReservedStorageConverter converter, ReservedStorageRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }

    public void deleteById(UUID reservedStorageId) {
        deleteById(uuidConverter.convertDomain(reservedStorageId));
    }

    public List<ReservedStorageModel> getPageByGameId(UUID gameId, Integer page, Integer itemsPerPage) {
        return converter.convertEntity(repository.getByGameId(uuidConverter.convertDomain(gameId), PageRequest.of(page, itemsPerPage)));
    }
}
