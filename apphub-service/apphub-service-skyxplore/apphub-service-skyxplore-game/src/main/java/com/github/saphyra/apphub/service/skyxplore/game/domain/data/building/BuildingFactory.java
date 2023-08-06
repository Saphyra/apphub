package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BuildingFactory {
    private final IdGenerator idGenerator;

    public Building create(String dataId, UUID location, UUID surfaceId, int level) {
        return Building.builder()
            .buildingId(idGenerator.randomUuid())
            .location(location)
            .surfaceId(surfaceId)
            .dataId(dataId)
            .level(level)
            .build();
    }

    public Building create(String dataId, UUID location, UUID surfaceId) {
        return create(dataId, location, surfaceId, 1);
    }
}
