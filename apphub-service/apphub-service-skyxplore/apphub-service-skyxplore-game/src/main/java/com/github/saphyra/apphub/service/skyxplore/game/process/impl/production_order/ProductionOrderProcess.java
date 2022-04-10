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
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
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
    private final Game game;
    @NonNull
    private final Planet planet;
    @NonNull
    private final AllocatedResource allocatedResource;
    @NonNull
    private final ReservedStorage reservedStorage;
    private final int amount;
    @NonNull
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public int getPriority() {
        return game.getProcesses()
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

        if (status == ProcessStatus.CREATED) {
            Optional<String> maybeProductionBuilding = applicationContextProxy.getBean(ProducerBuildingFinderService.class)
                .findProducerBuildingDataId(planet, reservedStorage.getDataId());
            log.info("ProductionBuilding for {}: {}", reservedStorage.getDataId(), maybeProductionBuilding);

            if (maybeProductionBuilding.isPresent()) {
                producerBuildingDataId = maybeProductionBuilding.get();

                log.info("Creating ResourceRequirementProcesses for {}", this);
                applicationContextProxy.getBean(ResourceRequirementProcessFactory.class)
                    .createResourceRequirementProcesses(syncCache, processId, game, planet, reservedStorage.getDataId(), amount, producerBuildingDataId)
                    .forEach(productionOrderProcess -> {
                        game.getProcesses().add(productionOrderProcess);
                        syncCache.saveGameItem(productionOrderProcess.toModel());
                    });

                status = ProcessStatus.IN_PROGRESS;
            } else {
                log.info("Producer not found for {}", reservedStorage.getDataId());
            }
        }

        if (status == ProcessStatus.IN_PROGRESS) {
            if (game.getProcesses().getByExternalReferenceAndType(processId, ProcessType.PRODUCTION_ORDER).stream().anyMatch(process -> process.getStatus() != ProcessStatus.DONE)) {
                log.info("Waiting for ProductionOrderProcesses to finish...");
                return;
            }

            List<? extends Process> requestWorkProcesses = game.getProcesses().getByExternalReferenceAndType(processId, ProcessType.REQUEST_WORK);
            if (requestWorkProcesses.isEmpty()) {
                log.info("RequestWorkProcess is not present. Resolving allocations...");

                applicationContextProxy.getBean(UseAllocatedResourceService.class)
                    .resolveAllocations(syncCache, game.getGameId(), planet, processId);

                requestWorkProcesses = applicationContextProxy.getBean(RequestWorkProcessFactoryForProductionOrder.class)
                    .createWorkPointProcesses(processId, game, planet, producerBuildingDataId, reservedStorage);

                game.getProcesses()
                    .addAll(requestWorkProcesses);

                requestWorkProcesses.forEach(process -> syncCache.saveGameItem(process.toModel()));
            }

            if (requestWorkProcesses.stream().allMatch(process -> process.getStatus() == ProcessStatus.DONE)) {
                applicationContextProxy.getBean(StoreResourceService.class)
                    .storeResource(syncCache, game, planet, reservedStorage, allocatedResource, amount);
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
        applicationContextProxy.getBean(AllocationRemovalService.class)
            .removeAllocationsAndReservations(syncCache, planet, processId);

        game.getProcesses()
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
        model.setGameId(game.getGameId());
        model.setType(GameItemType.PROCESS);
        model.setProcessType(getType());
        model.setStatus(status);
        model.setLocation(planet.getPlanetId());
        model.setLocationType(LocationType.PLANET.name());
        model.setExternalReference(getExternalReference());
        model.setData(new StringStringMap(
            CollectionUtils.toMap(
                new BiWrapper<>(ProcessParamKeys.PRODUCER_BUILDING_DATA_ID, producerBuildingDataId),
                new BiWrapper<>(ProcessParamKeys.RESERVED_STORAGE_ID, uuidConverter.convertDomain(reservedStorage.getReservedStorageId())),
                new BiWrapper<>(ProcessParamKeys.ALLOCATED_RESOURCE_ID, uuidConverter.convertDomain(allocatedResource.getAllocatedResourceId())),
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
