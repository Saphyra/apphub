package com.github.saphyra.apphub.service.skyxplore.game.tick.planet.processor;

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
//TODO unit test
public class ConstructionTickProcessor {
    private final FinishConstructionService finishConstructionService;
    private final ProceedWithConstructionService proceedWithConstructionService;
    private final ProduceResourcesService produceResourcesService;
    private final AllocatedResourceResolver allocatedResourceResolver;

    public void process(UUID gameId, Planet planet, Surface surface) {
        Construction construction = surface.getBuilding().getConstruction();
        if (!allResourcesPresent(planet.getStorageDetails(), construction.getConstructionId())) {
            produceResourcesService.produceResources(gameId, planet, construction);
        }

        if (allResourcesPresent(planet.getStorageDetails(), construction.getConstructionId())) {
            if (construction.getCurrentWorkPoints() == 0) {
                allocatedResourceResolver.resolveAllocations(gameId, planet, construction.getConstructionId());
            }

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
            .mapToInt(ReservedStorage::getAmount)
            .sum() == 0;
    }
}
