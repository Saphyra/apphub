package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building_module;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModuleModel;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

//TODO unit test
@Component
public class BuildingModuleDao extends AbstractDao<BuildingModuleEntity, BuildingModuleModel, String, BuildingModuleRepository> {
    private final UuidConverter uuidConverter;

    BuildingModuleDao(BuildingModuleConverter converter, BuildingModuleRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }

    public void deleteById(UUID buildingModuleId) {
        deleteById(uuidConverter.convertDomain(buildingModuleId));
    }

    public List<BuildingModuleModel> getPageByGameId(UUID gameId, Integer page, Integer itemsPerPage) {
        return converter.convertEntity(repository.getByGameId(uuidConverter.convertDomain(gameId), PageRequest.of(page, itemsPerPage)));
    }
}
