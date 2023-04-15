package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ProductionOrderService {
    private final ProductionOrderProcessFactory productionOrderProcessFactory;

    public void createProductionOrdersForReservedStorages(SyncCache syncCache, GameData gameData, UUID processId, UUID externalReference) {
        log.info("Creating ProductionOrderProcesses for Construction...");

        gameData.getReservedStorages()
            .getByExternalReference(externalReference)
            .stream()
            .flatMap(reservedStorage -> productionOrderProcessFactory.create(gameData, processId, reservedStorage.getLocation(), reservedStorage.getReservedStorageId()).stream())
            .forEach(productionOrderProcess -> {
                gameData.getProcesses().add(productionOrderProcess);
                syncCache.saveGameItem(productionOrderProcess.toModel());
            });

        log.info("ProductionOrderProcesses created.");
    }
}
