package com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
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
public class ProductionOrderProcessFactory {
    private final IdGenerator idGenerator;
    private final ResourceDataService resourceDataService;

    public List<ProductionOrderProcess> create(ApplicationContextProxy applicationContextProxy, UUID externalReference, Game game, Planet planet, UUID reservedStorageId) {
        ReservedStorage reservedStorage = planet.getStorageDetails()
            .getReservedStorages()
            .findById(reservedStorageId)
            .orElseThrow(() -> new IllegalStateException("ReservedStorage not found with id " + reservedStorageId));
        AllocatedResource allocatedResource = planet.getStorageDetails()
            .getAllocatedResources()
            .findByExternalReferenceAndDataIdValidated(reservedStorage.getExternalReference(), reservedStorage.getDataId());

        int maxBatchSize = resourceDataService.get(reservedStorage.getDataId()).getMaxProductionBatchSize();

        List<ProductionOrderProcess> result = new ArrayList<>();

        for (int processed = 0; processed < reservedStorage.getAmount(); processed += maxBatchSize) {
            int toCreate = Math.max(maxBatchSize, reservedStorage.getAmount() - processed);

            ProductionOrderProcess process = ProductionOrderProcess.builder()
                .processId(idGenerator.randomUuid())
                .externalReference(externalReference)
                .game(game)
                .planet(planet)
                .allocatedResource(allocatedResource)
                .reservedStorage(reservedStorage)
                .applicationContextProxy(applicationContextProxy)
                .amount(toCreate)
                .build();

            result.add(process);
        }


        return result;
    }
}
