package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ProductionOrderRequirementsMetCalculator {
    boolean areRequirementsMet(Planet planet, UUID externalReference) {
        boolean result = planet.getStorageDetails()
            .getReservedStorages()
            .stream()
            .filter(productionOrder -> productionOrder.getExternalReference().equals(externalReference))
            .mapToInt(ReservedStorage::getAmount)
            .sum() <= 0;
        log.debug("Are requirements met for externalReference {}: {}. ReservedStorages: {}", externalReference, result, planet.getStorageDetails().getReservedStorages());
        return result;
    }
}
