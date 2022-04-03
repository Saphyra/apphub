package com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessParamKeys;
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
public class ProductionOrderProcessFactory implements ProcessFactory {
    private final IdGenerator idGenerator;
    private final ResourceDataService resourceDataService;
    private final UuidConverter uuidConverter;
    private final ApplicationContextProxy applicationContextProxy;

    public List<ProductionOrderProcess> create(ApplicationContextProxy applicationContextProxy, UUID externalReference, Game game, Planet planet, UUID reservedStorageId) {
        ReservedStorage reservedStorage = planet.getStorageDetails()
            .getReservedStorages()
            .findById(reservedStorageId)
            .orElseThrow(() -> new IllegalStateException("ReservedStorage not found with id " + reservedStorageId));
        AllocatedResource allocatedResource = planet.getStorageDetails()
            .getAllocatedResources()
            .findByExternalReferenceAndDataIdValidated(reservedStorage.getExternalReference(), reservedStorage.getDataId());
        log.info("{} fond for {}", allocatedResource, reservedStorage);

        int maxBatchSize = resourceDataService.get(reservedStorage.getDataId()).getMaxProductionBatchSize();
        log.info("maxBatchSize: {}", maxBatchSize);

        List<ProductionOrderProcess> result = new ArrayList<>();

        for (int processed = 0; processed < reservedStorage.getAmount(); processed += maxBatchSize) {
            int toCreate = Math.min(maxBatchSize, reservedStorage.getAmount() - processed);
            log.info("Creating ProductionOrderProcess for {} amount of {}", toCreate, reservedStorage.getDataId());

            ProductionOrderProcess process = ProductionOrderProcess.builder()
                .processId(idGenerator.randomUuid())
                .status(ProcessStatus.CREATED)
                .externalReference(externalReference)
                .game(game)
                .planet(planet)
                .allocatedResource(allocatedResource)
                .reservedStorage(reservedStorage)
                .applicationContextProxy(applicationContextProxy)
                .amount(toCreate)
                .build();
            log.info("{} created.", process);

            result.add(process);
        }

        return result;
    }

    @Override
    public ProcessType getType() {
        return ProcessType.PRODUCTION_ORDER;
    }

    @Override
    public ProductionOrderProcess create(Game game, ProcessModel model) {
        Planet planet = game.getUniverse()
            .findPlanetByIdValidated(model.getLocation());

        AllocatedResource allocatedResource = planet.getStorageDetails()
            .getAllocatedResources()
            .findByIdValidated(uuidConverter.convertEntity(model.getData().get(ProcessParamKeys.ALLOCATED_RESOURCE_ID)));

        ReservedStorage reservedStorage = planet.getStorageDetails()
            .getReservedStorages()
            .findByIdValidated(uuidConverter.convertEntity(model.getData().get(ProcessParamKeys.RESERVED_STORAGE_ID)));

        return ProductionOrderProcess.builder()
            .processId(model.getId())
            .status(model.getStatus())
            .producerBuildingDataId(model.getData().get(ProcessParamKeys.PRODUCTION_BUILDING_DATA_ID))
            .externalReference(model.getExternalReference())
            .game(game)
            .planet(planet)
            .allocatedResource(allocatedResource)
            .reservedStorage(reservedStorage)
            .amount(Integer.parseInt(model.getData().get(ProcessParamKeys.AMOUNT)))
            .applicationContextProxy(applicationContextProxy)
            .build();
    }
}
