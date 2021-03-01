package com.github.saphyra.apphub.service.skyxplore.data.save_game.solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class SolarSystemDao extends AbstractDao<SolarSystemEntity, SolarSystemModel, String, SolarSystemRepository> {
    private final UuidConverter uuidConverter;

    public SolarSystemDao(SolarSystemConverter converter, SolarSystemRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }
}
