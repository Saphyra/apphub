package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.coordinate;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
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
public class CoordinateDao extends AbstractDao<CoordinateEntity, CoordinateModel, String, CoordinateRepository> {
    private final UuidConverter uuidConverter;

    public CoordinateDao(CoordinateConverter converter, CoordinateRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }

    public Optional<CoordinateModel> findById(UUID coordinateId) {
        return findById(uuidConverter.convertDomain(coordinateId));
    }

    public List<CoordinateModel> getByReferenceId(UUID referenceId) {
        return converter.convertEntity(repository.getByReferenceId(uuidConverter.convertDomain(referenceId)));
    }

    public void deleteById(UUID coordinateId) {
        deleteById(uuidConverter.convertDomain(coordinateId));
    }

    public List<CoordinateModel> getPageByGameId(UUID gameId, Integer page, Integer itemsPerPage) {
        return converter.convertEntity(repository.getByGameId(uuidConverter.convertDomain(gameId), PageRequest.of(page, itemsPerPage)));
    }
}
