package com.github.saphyra.apphub.service.skyxplore.data.save_game.reserved_storage;

import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

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
}
