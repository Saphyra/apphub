package com.github.saphyra.apphub.service.skyxplore.game.process.impl.construction;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcessType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class RequestWorkProcessFactoryForConstruction {
    private final BuildingDataService buildingDataService;
    private final RequestWorkProcessFactory requestWorkProcessFactory;

    List<RequestWorkProcess> createRequestWorkProcesses(UUID processId, Game game, Planet planet, Building building) {
        log.info("Creating RequestWorkProcesses...");
        ConstructionRequirements constructionRequirements = buildingDataService.get(building.getDataId())
            .getConstructionRequirements()
            .get(building.getLevel() + 1);
        log.info("{}", constructionRequirements);

        return requestWorkProcessFactory.create(
            game,
            processId,
            planet,
            building.getConstruction().getConstructionId(),
            RequestWorkProcessType.CONSTRUCTION,
            SkillType.BUILDING,
            constructionRequirements.getRequiredWorkPoints(),
            constructionRequirements.getParallelWorkers()
        );
    }
}
