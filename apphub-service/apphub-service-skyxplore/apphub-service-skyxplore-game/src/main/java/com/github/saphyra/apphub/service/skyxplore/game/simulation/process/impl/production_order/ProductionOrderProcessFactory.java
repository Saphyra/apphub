package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessParamKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductionOrderProcessFactory implements ProcessFactory {
    private final IdGenerator idGenerator;
    private final ResourceDataService resourceDataService;
    private final UuidConverter uuidConverter;
    private final ApplicationContextProxy applicationContextProxy;

    public List<ProductionOrderProcess> create(GameData gameData, UUID externalReference, UUID location, UUID reservedStorageId) {
        ReservedStorage reservedStorage = gameData.getReservedStorages()
            .findByReservedStorageId(reservedStorageId)
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "ReservedStorage not found with id " + reservedStorageId));

        return create(gameData, externalReference, location, reservedStorage);
    }

    public List<ProductionOrderProcess> create(GameData gameData, UUID externalReference, UUID location, ReservedStorage reservedStorage) {
        UUID allocatedResourceId = gameData.getAllocatedResources()
            .findByExternalReferenceAndDataId(reservedStorage.getExternalReference(), reservedStorage.getDataId())
            .map(AllocatedResource::getAllocatedResourceId)
            .orElse(null);

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
                .gameData(gameData)
                .location(location)
                .allocatedResourceId(allocatedResourceId)
                .reservedStorageId(reservedStorage.getReservedStorageId())
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
    public ProductionOrderProcess createFromModel(Game game, ProcessModel model) {
        return ProductionOrderProcess.builder()
            .processId(model.getId())
            .status(model.getStatus())
            .producerBuildingDataId(model.getData().get(ProcessParamKeys.PRODUCER_BUILDING_DATA_ID))
            .externalReference(model.getExternalReference())
            .gameData(game.getData())
            .location(model.getLocation())
            .allocatedResourceId(uuidConverter.convertEntity(model.getData().get(ProcessParamKeys.ALLOCATED_RESOURCE_ID)))
            .reservedStorageId(uuidConverter.convertEntity(model.getData().get(ProcessParamKeys.RESERVED_STORAGE_ID)))
            .amount(Integer.parseInt(model.getData().get(ProcessParamKeys.AMOUNT)))
            .applicationContextProxy(applicationContextProxy)
            .build();
    }
}
