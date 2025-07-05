package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
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

    public Optional<SolarSystemModel> findById(UUID solarSystemId) {
        return findById(uuidConverter.convertDomain(solarSystemId));
    }

    public List<SolarSystemModel> getByGameId(UUID gameId, Integer page, Integer itemsPerPage) {
        return converter.convertEntity(repository.getByGameId(uuidConverter.convertDomain(gameId), PageRequest.of(page, itemsPerPage)));
    }

    public void deleteById(UUID solarSystemId) {
        deleteById(uuidConverter.convertDomain(solarSystemId));
    }
}
