package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.building;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class BuildingFactory {
    private final IdGenerator idGenerator;

    public Building create(String dataId) {
        return Building.builder()
            .buildingId(idGenerator.randomUuid())
            .dataId(dataId)
            .level(1)
            .build();
    }
}
