package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BuildingModuleFactory {
    private final IdGenerator idGenerator;

    public BuildingModule create(UUID location, UUID constructionAreaId, String dataId) {
        return BuildingModule.builder()
            .buildingModuleId(idGenerator.randomUuid())
            .location(location)
            .constructionAreaId(constructionAreaId)
            .dataId(dataId)
            .build();
    }
}
