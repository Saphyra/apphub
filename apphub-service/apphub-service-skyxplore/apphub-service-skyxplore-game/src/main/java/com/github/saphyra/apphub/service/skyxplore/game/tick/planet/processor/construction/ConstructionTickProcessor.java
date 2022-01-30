package com.github.saphyra.apphub.service.skyxplore.game.tick.planet.processor.construction;

import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.tick.production.AllocatedResourceResolver;
import com.github.saphyra.apphub.service.skyxplore.game.tick.production.ProduceResourcesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConstructionTickProcessor {
    private final FinishConstructionService finishConstructionService;
    private final ProceedWithConstructionService proceedWithConstructionService;
    private final ProduceResourcesService produceResourcesService;
    private final AllocatedResourceResolver allocatedResourceResolver;

    public void process(UUID gameId, Planet planet, Surface surface) {
        Construction construction = surface.getBuilding()
            .getConstruction();
        log.debug("Working on {} in game {}", construction, gameId);
        if (!allResourcesPresent(planet.getStorageDetails(), construction.getConstructionId())) {
            log.debug("There are missing resources to start working on {} in game {}", construction, gameId);
            produceResourcesService.produceResources(gameId, planet, construction);
        }

        if (allResourcesPresent(planet.getStorageDetails(), construction.getConstructionId())) {
            log.debug("Resources are present to work on {} in game {}", construction, gameId);

            if (construction.getCurrentWorkPoints() == 0) {
                log.debug("Working on {} is not started yet in game {}", construction, gameId);
                allocatedResourceResolver.resolveAllocations(gameId, planet, construction.getConstructionId());
            }

            log.debug("Proceeding with {} in game {}", construction, gameId);
            proceedWithConstructionService.proceedWithConstruction(gameId, planet, surface, construction);
        }

        if (construction.getCurrentWorkPoints() >= construction.getRequiredWorkPoints()) {
            finishConstructionService.finishConstruction(gameId, planet, surface);
        }
    }

    private boolean allResourcesPresent(StorageDetails storageDetails, UUID constructionId) {
        return storageDetails.getReservedStorages()
            .stream()
            .filter(reservedStorage -> reservedStorage.getExternalReference().equals(constructionId))
            .peek(reservedStorage -> log.debug("ReservedStorage found for constructionId {}: {}", constructionId, reservedStorage))
            .mapToInt(ReservedStorage::getAmount)
            .sum() == 0;
    }
}
