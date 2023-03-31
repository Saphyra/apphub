package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.citizen;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class CitizenDao extends AbstractDao<CitizenEntity, CitizenModel, String, CitizenRepository> {
    private final UuidConverter uuidConverter;

    public CitizenDao(CitizenConverter converter, CitizenRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }

    public Optional<CitizenModel> findById(UUID citizenId) {
        return findById(uuidConverter.convertDomain(citizenId));
    }

    public List<CitizenModel> getByLocation(UUID location) {
        return converter.convertEntity(repository.getByLocation(uuidConverter.convertDomain(location)));
    }

    public void deleteById(UUID citizenId) {
        deleteById(uuidConverter.convertDomain(citizenId));
    }

    public List<CitizenModel> getPageByGameId(UUID gameId, Integer page, Integer itemsPerPage) {
        return converter.convertEntity(repository.getByGameId(uuidConverter.convertDomain(gameId), PageRequest.of(page, itemsPerPage)));
    }
}
