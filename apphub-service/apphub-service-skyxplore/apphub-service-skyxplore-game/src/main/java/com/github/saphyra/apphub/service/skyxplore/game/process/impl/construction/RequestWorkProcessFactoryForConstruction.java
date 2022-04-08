package com.github.saphyra.apphub.service.skyxplore.game.process.impl.construction;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcessType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class RequestWorkProcessFactoryForConstruction {
    private final BuildingDataService buildingDataService;
    private final RequestWorkProcessFactory requestWorkProcessFactory;

    List<RequestWorkProcess> createRequestWorkProcesses(SyncCache syncCache, UUID processId, Game game, Planet planet, Building building) {
        log.info("Creating RequestWorkProcesses...");
        ConstructionRequirements constructionRequirements = buildingDataService.get(building.getDataId())
            .getConstructionRequirements()
            .get(building.getLevel() + 1);
        log.info("{}", constructionRequirements);

        int workPointsPerWorker = constructionRequirements.getRequiredWorkPoints() / constructionRequirements.getParallelWorkers();
        log.info("WorkPointsPerWorker: {}", workPointsPerWorker);

        List<RequestWorkProcess> result = new ArrayList<>();

        for (int i = 0; i < constructionRequirements.getParallelWorkers(); i++) {
            RequestWorkProcess requestWorkProcess = requestWorkProcessFactory.create(
                processId,
                game,
                planet,
                null,
                SkillType.BUILDING,
                workPointsPerWorker,
                RequestWorkProcessType.CONSTRUCTION,
                building.getConstruction().getConstructionId()
            );
            log.info("{} created.", requestWorkProcess);
            result.add(requestWorkProcess);
        }
        log.info("RequestWorkProcesses created.");

        return result;
    }
}
