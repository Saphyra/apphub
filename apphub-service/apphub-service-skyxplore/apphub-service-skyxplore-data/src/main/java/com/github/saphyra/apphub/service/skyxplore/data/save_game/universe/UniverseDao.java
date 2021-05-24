package com.github.saphyra.apphub.service.skyxplore.data.save_game.universe;

import com.github.saphyra.apphub.api.skyxplore.model.game.UniverseModel;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UniverseDao extends AbstractDao<UniverseEntity, UniverseModel, String, UniverseRepository> {
    private final UuidConverter uuidConverter;

    public UniverseDao(UniverseConverter converter, UniverseRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteById(UUID gameId) {
        deleteById(uuidConverter.convertDomain(gameId));
    }
}
