package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.surface;

import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class SurfaceDao extends AbstractDao<SurfaceEntity, SurfaceModel, String, SurfaceRepository> {
    private final UuidConverter uuidConverter;

    public SurfaceDao(SurfaceConverter converter, SurfaceRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }

    public Optional<SurfaceModel> findById(UUID surfaceId) {
        return findById(uuidConverter.convertDomain(surfaceId));
    }

    public List<SurfaceModel> getByPlanetId(UUID planetId) {
        return converter.convertEntity(repository.getByPlanetId(uuidConverter.convertDomain(planetId)));
    }

    public void deleteById(UUID surfaceId) {
        deleteById(uuidConverter.convertDomain(surfaceId));
    }

    public List<SurfaceModel> getPageByGameId(UUID gameId, Integer page, Integer itemsPerPage) {
        return converter.convertEntity(repository.getByGameId(uuidConverter.convertDomain(gameId), PageRequest.of(page, itemsPerPage)));
    }
}
