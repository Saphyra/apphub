package com.github.saphyra.apphub.service.skyxplore.data.save_game.planet;

import com.github.saphyra.apphub.api.skyxplore.model.game.PlanetModel;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PlanetDao extends AbstractDao<PlanetEntity, PlanetModel, String, PlanetRepository> {
    private final UuidConverter uuidConverter;

    public PlanetDao(PlanetConverter converter, PlanetRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }
}
