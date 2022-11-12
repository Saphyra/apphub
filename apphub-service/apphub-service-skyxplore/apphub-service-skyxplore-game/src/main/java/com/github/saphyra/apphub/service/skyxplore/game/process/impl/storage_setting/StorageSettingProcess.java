package com.github.saphyra.apphub.service.skyxplore.game.process.impl.storage_setting;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order.ProductionOrderProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order.ProductionOrderProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.ReservedStorageFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.FreeStorageQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ReservedStorageToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(access = AccessLevel.PACKAGE)
@Slf4j
public class StorageSettingProcess implements Process {
    @Getter
    @NonNull
    private final UUID processId;

    @Getter
    @NonNull
    private volatile ProcessStatus status;

    @NonNull
    private final Game game;
    @NonNull
    private final Planet planet;
    @NonNull
    private final StorageSetting storageSetting;

    @NonNull
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public UUID getExternalReference() {
        return storageSetting.getStorageSettingId();
    }

    @Override
    public int getPriority() {
        return planet.getPriorities().get(PriorityType.MANUFACTURING) * storageSetting.getPriority() * GameConstants.PROCESS_PRIORITY_MULTIPLIER;
    }

    @Override
    public ProcessType getType() {
        return ProcessType.STORAGE_SETTING;
    }

    @Override
    public void work(SyncCache syncCache) {
        log.info("Working on {}", this);

        if (status == ProcessStatus.CREATED) {
            status = ProcessStatus.IN_PROGRESS;
        }

        if (status == ProcessStatus.IN_PROGRESS) {
            handleInProgress(syncCache);
        }

        cleanUpReservedStorages(syncCache);
        cleanUpFinishedProductionOrders(syncCache);
    }

    private void handleInProgress(SyncCache syncCache) {
        int storedAmount = getStoredAmount();
        if (storedAmount < storageSetting.getTargetAmount()) {
            List<ProductionOrderProcess> processes = game.getProcesses()
                .getByExternalReferenceAndType(processId, ProcessType.PRODUCTION_ORDER)
                .stream()
                .map(p -> (ProductionOrderProcess) p)
                .collect(Collectors.toList());
            int producedAmount = processes.stream()
                .mapToInt(ProductionOrderProcess::getAmount)
                .sum();
            int missingAmount = storageSetting.getTargetAmount() - producedAmount - storedAmount;

            log.info("ProducedAmount: {}, storedAmount: {}, missingAmount: {}", producedAmount, storedAmount, missingAmount);

            if (missingAmount > 0) {
                int availableStorage = calculateAvailableStorage();

                log.info("{} of {} is missing in planet {}. Initiating production... Available storage: {}", missingAmount, storageSetting.getDataId(), planet.getPlanetId(), availableStorage);

                if (availableStorage > 0) {
                    int reservedAmount = Math.min(missingAmount, availableStorage);

                    ReservedStorage reservedStorage = applicationContextProxy.getBean(ReservedStorageFactory.class)
                        .create(planet.getPlanetId(), LocationType.PLANET, processId, storageSetting.getDataId(), reservedAmount);
                    planet.getStorageDetails()
                        .getReservedStorages()
                        .add(reservedStorage);
                    syncCache.saveGameItem(applicationContextProxy.getBean(ReservedStorageToModelConverter.class)
                        .convert(reservedStorage, game.getGameId()));

                    syncCache.addMessage(
                        planet.getOwner(),
                        WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED,
                        planet.getPlanetId(),
                        () -> applicationContextProxy.getBean(WsMessageSender.class).planetStorageModified(
                            planet.getOwner(),
                            planet.getPlanetId(),
                            applicationContextProxy.getBean(PlanetStorageOverviewQueryService.class).getStorage(planet)
                        )
                    );

                    applicationContextProxy.getBean(ProductionOrderProcessFactory.class)
                        .create(processId, game, planet, reservedStorage)
                        .forEach(productionOrderProcess -> {
                            game.getProcesses().add(productionOrderProcess);
                            syncCache.saveGameItem(productionOrderProcess.toModel());
                        });
                }
            } else {
                log.info("All the missing {} is being produced in planet {}", storageSetting.getDataId(), planet.getPlanetId());
            }
        } else {
            log.info("There is enough {} in planet {}", storageSetting.getDataId(), planet.getPlanetId());
        }
    }

    private int getStoredAmount() {
        return planet.getStorageDetails()
            .getStoredResources()
            .get(storageSetting.getDataId())
            .getAmount();
    }

    private int calculateAvailableStorage() {
        StorageType storageType = applicationContextProxy.getBean(ResourceDataService.class)
            .get(storageSetting.getDataId())
            .getStorageType();

        return applicationContextProxy.getBean(FreeStorageQueryService.class)
            .getFreeStorage(planet, storageType);
    }

    private void cleanUpReservedStorages(SyncCache syncCache) {
        List<ReservedStorage> reservedStorages = planet.getStorageDetails()
            .getReservedStorages()
            .getByExternalReference(processId)
            .stream()
            .filter(reservedStorage -> reservedStorage.getAmount() == 0)
            .collect(Collectors.toList());

        planet.getStorageDetails()
            .getReservedStorages()
            .removeIf(reservedStorages::contains);

        reservedStorages.forEach(reservedStorage -> syncCache.deleteGameItem(reservedStorage.getReservedStorageId(), GameItemType.RESERVED_STORAGE));
    }

    private void cleanUpFinishedProductionOrders(SyncCache syncCache) {
        game.getProcesses()
            .getByExternalReferenceAndType(processId, ProcessType.PRODUCTION_ORDER)
            .stream()
            .filter(process -> process.getStatus() == ProcessStatus.DONE)
            .forEach(process -> process.cleanup(syncCache));
    }

    @Override
    public void cancel(SyncCache syncCache) {
        cleanup(syncCache);
    }

    @Override
    public void cleanup(SyncCache syncCache) {
        log.info("Cleaning up {}", this);

        game.getProcesses()
            .getByExternalReference(processId)
            .forEach(process -> process.cleanup(syncCache));

        status = ProcessStatus.READY_TO_DELETE;

        syncCache.saveGameItem(toModel());

        ReservedStorages reservedStorages = planet.getStorageDetails()
            .getReservedStorages();
        List<ReservedStorage> rs = reservedStorages
            .getByExternalReference(processId);
        reservedStorages.removeIf(rs::contains);
        rs.forEach(reservedStorage -> syncCache.deleteGameItem(reservedStorage.getReservedStorageId(), GameItemType.RESERVED_STORAGE));

        planet.getStorageDetails()
            .getStorageSettings()
            .deleteByStorageSettingId(storageSetting.getStorageSettingId());
        syncCache.deleteGameItem(storageSetting.getStorageSettingId(), GameItemType.STORAGE_SETTING);

        syncCache.addMessage(
            planet.getOwner(),
            WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED,
            planet.getPlanetId(),
            () -> applicationContextProxy.getBean(WsMessageSender.class).planetStorageModified(
                planet.getOwner(),
                planet.getPlanetId(),
                applicationContextProxy.getBean(PlanetStorageOverviewQueryService.class).getStorage(planet)
            )
        );
    }

    @Override
    public ProcessModel toModel() {
        ProcessModel model = new ProcessModel();
        model.setId(processId);
        model.setGameId(game.getGameId());
        model.setType(GameItemType.PROCESS);
        model.setProcessType(getType());
        model.setStatus(status);
        model.setLocation(planet.getPlanetId());
        model.setLocationType(LocationType.PLANET.name());
        model.setExternalReference(getExternalReference());
        return model;
    }

    @Override
    public String toString() {
        return String.format(
            "%s(processId=%s, status=%s, storageSetting=%s)",
            getClass().getSimpleName(),
            processId,
            status,
            storageSetting
        );
    }
}
