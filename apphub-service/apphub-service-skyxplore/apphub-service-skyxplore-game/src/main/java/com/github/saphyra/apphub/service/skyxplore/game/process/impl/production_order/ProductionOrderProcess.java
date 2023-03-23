package com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order;

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
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessParamKeys;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.UseAllocatedResourceService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    private volatile String producerBuildingDataId;

    @Getter
    @NonNull
    private final UUID externalReference;
    @NonNull
    private final GameData gameData;
    @NonNull
    private final UUID location;
    private final AllocatedResource allocatedResource;
    @NonNull
    private final ReservedStorage reservedStorage;

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

    @Override
    public void work(SyncCache syncCache) {
        log.info("Working on {}", this);

        UUID ownerId = gameData.getPlanets()
            .get(location)
            .getOwner();

        if (status == ProcessStatus.CREATED) {
            Optional<String> maybeProductionBuilding = applicationContextProxy.getBean(ProducerBuildingFinderService.class)
                .findProducerBuildingDataId(gameData, location, reservedStorage.getDataId());
            log.info("ProductionBuilding for {}: {}", reservedStorage.getDataId(), maybeProductionBuilding);

            if (maybeProductionBuilding.isPresent()) {
                producerBuildingDataId = maybeProductionBuilding.get();

                log.info("Creating ResourceRequirementProcesses for {}", this);
                applicationContextProxy.getBean(ResourceRequirementProcessFactory.class)
                    .createResourceRequirementProcesses(syncCache, processId, gameData, location, ownerId, reservedStorage.getDataId(), amount, producerBuildingDataId)
                    .forEach(productionOrderProcess -> {
                        gameData.getProcesses().add(productionOrderProcess);
                        syncCache.saveGameItem(productionOrderProcess.toModel());
                    });

                status = ProcessStatus.IN_PROGRESS;
            } else {
                log.info("Producer not found for {}", reservedStorage.getDataId());
            }
        }

        if (status == ProcessStatus.IN_PROGRESS) {
            if (gameData.getProcesses().getByExternalReferenceAndType(processId, ProcessType.PRODUCTION_ORDER).stream().anyMatch(process -> process.getStatus() != ProcessStatus.DONE)) {
                log.info("Waiting for ProductionOrderProcesses to finish...");
                return;
            }

            List<? extends Process> requestWorkProcesses = gameData.getProcesses().getByExternalReferenceAndType(processId, ProcessType.REQUEST_WORK);
            if (requestWorkProcesses.isEmpty()) {
                log.info("RequestWorkProcess is not present. Resolving allocations...");

                applicationContextProxy.getBean(UseAllocatedResourceService.class)
                    .resolveAllocations(syncCache, gameData.getGameId(), gameData, location, ownerId, processId);

                requestWorkProcesses = applicationContextProxy.getBean(RequestWorkProcessFactoryForProductionOrder.class)
                    .createWorkPointProcesses(processId, gameData, location, producerBuildingDataId, reservedStorage);

                gameData.getProcesses()
                    .addAll(requestWorkProcesses);

                requestWorkProcesses.forEach(process -> syncCache.saveGameItem(process.toModel()));
            }

            if (requestWorkProcesses.stream().allMatch(process -> process.getStatus() == ProcessStatus.DONE)) {
                applicationContextProxy.getBean(StoreResourceService.class)
                    .storeResource(syncCache, gameData, location, ownerId, reservedStorage, allocatedResource, amount);
                status = ProcessStatus.DONE;
            } else {
                log.info("Waiting for RequestWorkProcesses to finish...");
            }
        }
    }

    @Override
    public void cancel(SyncCache syncCache) {
        cleanup(syncCache);
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
                new BiWrapper<>(ProcessParamKeys.RESERVED_STORAGE_ID, uuidConverter.convertDomain(reservedStorage.getReservedStorageId())),
                new BiWrapper<>(ProcessParamKeys.ALLOCATED_RESOURCE_ID, uuidConverter.convertDomain(allocatedResource, AllocatedResource::getAllocatedResourceId)),
                new BiWrapper<>(ProcessParamKeys.AMOUNT, String.valueOf(amount))
            ))
        );
        return model;
    }

    @Override
    public String toString() {
        return String.format(
            "%s(processId=%s, status=%s, producerBuildingDataId=%s, reservedStorageId=%s, dataId=%s, amount=%s)",
            getClass().getSimpleName(),
            processId,
            status,
            producerBuildingDataId,
            reservedStorage.getReservedStorageId(),
            reservedStorage.getDataId(),
            amount
        );
    }
}
