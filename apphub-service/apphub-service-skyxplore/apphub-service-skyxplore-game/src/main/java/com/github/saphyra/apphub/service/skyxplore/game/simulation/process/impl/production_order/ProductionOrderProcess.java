package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.StringStringMap;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.UUID;

import static java.util.Objects.isNull;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(access = AccessLevel.PACKAGE)
@Slf4j
public class ProductionOrderProcess implements Process {
    @Getter
    @NonNull
    private final UUID processId;

    @Getter
    @NonNull
    private volatile ProcessStatus status;

    @Getter
    @Nullable
    private volatile String producerBuildingDataId;
    private volatile UUID allocatedResourceId;
    @NonNull
    private volatile UUID reservedStorageId;

    @Getter
    @NonNull
    private final UUID externalReference;
    @NonNull
    private final GameData gameData;
    @NonNull
    private final UUID location;
    @Getter
    private final int amount;
    @NonNull
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public int getPriority() {
        return gameData.getProcesses()
            .findByIdValidated(externalReference)
            .getPriority() + 1;
    }

    @Override
    public ProcessType getType() {
        return ProcessType.PRODUCTION_ORDER;
    }

    private ReservedStorage findReservedStorage() {
        return gameData.getReservedStorages()
            .findByReservedStorageIdValidated(reservedStorageId);
    }

    @Override
    public void work(SyncCache syncCache) {
        log.info("Working on {}", this);

        ProductionOrderProcessHelper helper = applicationContextProxy.getBean(ProductionOrderProcessHelper.class);

        if (status == ProcessStatus.CREATED) {
            String dataId = findReservedStorage()
                .getDataId();
            producerBuildingDataId = helper.findProductionBuilding(gameData, location, dataId);

            if (!isNull(producerBuildingDataId)) {
                log.info("Creating ResourceRequirementProcesses for {}", this);


                helper.processResourceRequirements(syncCache, gameData, processId, location, dataId, amount, producerBuildingDataId);
                status = ProcessStatus.IN_PROGRESS;
            } else {
                log.info("Available ProductionBuilding not found for {}", dataId);
            }
        }

        ProductionOrderProcessConditions conditions = applicationContextProxy.getBean(ProductionOrderProcessConditions.class);

        if (status == ProcessStatus.IN_PROGRESS) {
            if (!conditions.requiredResourcesPresent(gameData, processId)) {
                log.info("Waiting for ProductionOrderProcesses to finish...");
                return;
            }

            if (!conditions.workStarted(gameData, processId)) {
                helper.startWork(syncCache, gameData, processId, producerBuildingDataId, reservedStorageId);
            }

            if (conditions.workDone(gameData, processId)) {
                helper.storeResource(syncCache, gameData, location, reservedStorageId, allocatedResourceId, amount);

                status = ProcessStatus.DONE;
            } else {
                log.info("Waiting for RequestWorkProcesses to finish...");
            }
        }
    }

    @Override
    public void cleanup(SyncCache syncCache) {
        log.info("Cleaning up {}", this);

        UUID ownerId = gameData.getPlanets()
            .get(location)
            .getOwner();

        applicationContextProxy.getBean(AllocationRemovalService.class)
            .removeAllocationsAndReservations(syncCache, gameData, location, ownerId, processId);

        gameData.getProcesses()
            .getByExternalReference(processId)
            .forEach(process -> process.cleanup(syncCache));

        status = ProcessStatus.READY_TO_DELETE;

        syncCache.saveGameItem(toModel());
    }

    @Override
    public ProcessModel toModel() {
        UuidConverter uuidConverter = applicationContextProxy.getBean(UuidConverter.class);

        ProcessModel model = new ProcessModel();
        model.setId(processId);
        model.setGameId(gameData.getGameId());
        model.setType(GameItemType.PROCESS);
        model.setProcessType(getType());
        model.setStatus(status);
        model.setLocation(location);
        model.setExternalReference(getExternalReference());
        model.setData(new StringStringMap(
            CollectionUtils.toMap(
                new BiWrapper<>(ProcessParamKeys.PRODUCER_BUILDING_DATA_ID, producerBuildingDataId),
                new BiWrapper<>(ProcessParamKeys.RESERVED_STORAGE_ID, uuidConverter.convertDomain(reservedStorageId)),
                new BiWrapper<>(ProcessParamKeys.ALLOCATED_RESOURCE_ID, uuidConverter.convertDomain(allocatedResourceId)),
                new BiWrapper<>(ProcessParamKeys.AMOUNT, String.valueOf(amount))
            ))
        );
        return model;
    }

    @Override
    public String toString() {
        return String.format(
            "%s(processId=%s, status=%s, producerBuildingDataId=%s, reservedStorageId=%s, amount=%s)",
            getClass().getSimpleName(),
            processId,
            status,
            producerBuildingDataId,
            reservedStorageId,
            amount
        );
    }
}
