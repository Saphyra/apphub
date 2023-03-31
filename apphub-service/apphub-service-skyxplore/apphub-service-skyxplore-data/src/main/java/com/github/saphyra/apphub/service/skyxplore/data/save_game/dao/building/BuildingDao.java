package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class BuildingDao extends AbstractDao<BuildingEntity, BuildingModel, String, BuildingRepository> {
    private final UuidConverter uuidConverter;

    public BuildingDao(BuildingConverter converter, BuildingRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }

    public Optional<BuildingModel> findById(UUID buildingId) {
        return findById(uuidConverter.convertDomain(buildingId));
    }

    public Optional<BuildingModel> findBySurfaceId(UUID surfaceId) {
        return converter.convertEntity(repository.findBySurfaceId(uuidConverter.convertDomain(surfaceId)));
    }

    public void deleteById(UUID buildingId) {
        deleteById(uuidConverter.convertDomain(buildingId));
    }

    public List<BuildingModel> getPageByGameId(UUID gameId, Integer page, Integer itemsPerPage) {
        return converter.convertEntity(repository.getByGameId(uuidConverter.convertDomain(gameId), PageRequest.of(page, itemsPerPage)));
    }
}
