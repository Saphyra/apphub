package com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionData;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class RequestWorkProcessFactoryForProductionOrder {
    private final ProductionBuildingService productionBuildingService;
    private final GameProperties gameProperties;
    private final RequestWorkProcessFactory requestWorkProcessFactory;

    List<RequestWorkProcess> createWorkPointProcesses(UUID processId, Game game, Planet planet, String producerBuildingDataId, ReservedStorage reservedStorage) {
        log.info("Creating WorkPointProcesses...");

        ProductionData productionData = productionBuildingService.get(producerBuildingDataId)
            .getGives()
            .get(reservedStorage.getDataId());
        ConstructionRequirements constructionRequirements = productionData.getConstructionRequirements();
        int requiredWorkPoints = reservedStorage.getAmount() * constructionRequirements.getRequiredWorkPoints();

        int maxWorkPointsBatch = gameProperties.getCitizen()
            .getMaxWorkPointsBatch();

        log.info("RequiredWorkPoints: {}, MaxWorkPointsBatch: {}", requiredWorkPoints, maxWorkPointsBatch);

        List<RequestWorkProcess> result = new ArrayList<>();

        for (int workPointsLeft = requiredWorkPoints; workPointsLeft > 0; workPointsLeft -= maxWorkPointsBatch) {
            RequestWorkProcess requestWorkProcess = requestWorkProcessFactory.create(
                processId,
                game,
                planet,
                producerBuildingDataId,
                productionData.getRequiredSkill(),
                Math.min(workPointsLeft, maxWorkPointsBatch)
            );
            log.info("{} created.", requestWorkProcess);
            result.add(requestWorkProcess);
        }

        return result;
    }
}
