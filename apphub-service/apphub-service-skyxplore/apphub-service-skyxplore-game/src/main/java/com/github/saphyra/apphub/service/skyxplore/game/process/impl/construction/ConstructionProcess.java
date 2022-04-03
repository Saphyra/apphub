package com.github.saphyra.apphub.service.skyxplore.game.process.impl.construction;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.UseAllocatedResourceService;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order.ProductionOrderProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.BuildingToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

//TODO unit test
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
@Slf4j
public class ConstructionProcess implements Process {
    @Getter
    @NonNull
    private final UUID processId;

    @Getter
    private volatile ProcessStatus status;

    private final Game game;
    private final Planet planet;
    private final Building building;
    private final Construction construction;

    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public UUID getExternalReference() {
        return construction.getConstructionId();
    }

    @Override
    public int getPriority() {
        return planet.getPriorities().get(PriorityType.CONSTRUCTION) * construction.getPriority() * GameConstants.PROCESS_PRIORITY_MULTIPLIER;
    }

    @Override
    public ProcessType getType() {
        return ProcessType.CONSTRUCTION;
    }

    @Override
    public void work(SyncCache syncCache) {
        log.info("Working on {}", this);

        if (status == ProcessStatus.CREATED) {
            createProductionOrderProcesses(syncCache);

            status = ProcessStatus.IN_PROGRESS;
        }

        List<Process> productionOrderProcesses = game.getProcesses().getByExternalReferenceAndType(processId, ProcessType.PRODUCTION_ORDER);
        if (productionOrderProcesses.stream().anyMatch(process -> process.getStatus() != ProcessStatus.DONE)) {
            log.info("Waiting for ProductionOrderProcesses to finish...");
            return;
        }

        List<Process> workProcesses = game.getProcesses().getByExternalReferenceAndType(processId, ProcessType.REQUEST_WORK);
        if (workProcesses.isEmpty()) {
            applicationContextProxy.getBean(UseAllocatedResourceService.class)
                .resolveAllocations(syncCache, game.getGameId(), planet, construction.getConstructionId());
            createRequestWorkProcesses(syncCache);
        } else if (workProcesses.stream().allMatch(process -> process.getStatus() == ProcessStatus.DONE)) {
            finishConstruction(syncCache);

            status = ProcessStatus.DONE;
        } else {
            log.info("Waiting for RequestWorkProcesses to finish...");
        }

        if (status == ProcessStatus.DONE) {
            productionOrderProcesses.forEach(process -> process.cleanup(syncCache));
            workProcesses.forEach(process -> process.cleanup(syncCache));

            status = ProcessStatus.READY_TO_DELETE;
        }
    }

    //TODO extract
    private void createRequestWorkProcesses(SyncCache syncCache) {
        log.info("Creating RequestWorkProcesses...");
        ConstructionRequirements constructionRequirements = applicationContextProxy.getBean(BuildingDataService.class)
            .get(building.getDataId())
            .getConstructionRequirements()
            .get(building.getLevel() + 1);
        log.info("{}", constructionRequirements);

        int workPointsPerWorker = constructionRequirements.getRequiredWorkPoints() / constructionRequirements.getParallelWorkers();
        log.info("WorkPointsPerWorker: {}", workPointsPerWorker);

        for (int i = 0; i < constructionRequirements.getParallelWorkers(); i++) {
            RequestWorkProcess requestWorkProcess = applicationContextProxy.getBean(RequestWorkProcessFactory.class)
                .create(
                    processId,
                    game,
                    planet,
                    null,
                    SkillType.BUILDING,
                    workPointsPerWorker,
                    RequestWorkProcessType.CONSTRUCTION,
                    construction.getConstructionId()
                );
            log.info("{} created.", requestWorkProcess);
            game.getProcesses()
                .add(requestWorkProcess);
            syncCache.saveGameItem(requestWorkProcess.toModel());
        }
        log.info("RequestWorkProcesses created.");
    }

    //TODO extract
    private void createProductionOrderProcesses(SyncCache syncCache) {
        log.info("Creating ProductionOrderProcesses...");

        planet.getStorageDetails()
            .getReservedStorages()
            .stream()
            .filter(reservedStorage -> reservedStorage.getExternalReference().equals(construction.getConstructionId()))
            .peek(reservedStorage -> log.info("{}", reservedStorage))
            .flatMap(reservedStorage -> applicationContextProxy.getBean(ProductionOrderProcessFactory.class)
                .create(applicationContextProxy, processId, game, planet, reservedStorage.getReservedStorageId()).stream()
            )
            .forEach(productionOrderProcess -> {
                game.getProcesses().add(productionOrderProcess);
                syncCache.saveGameItem(productionOrderProcess.toModel());
            });
        log.info("ProductionOrderProcesses created.");
    }

    @Override
    public void cancel(SyncCache syncCache) {
        game.getProcesses()
            .getByExternalReference(processId)
            .forEach(process -> process.cancel(syncCache));

        building.setConstruction(null);
        syncCache.deleteGameItem(construction.getConstructionId(), GameItemType.CONSTRUCTION);
        syncCache.saveGameItem(applicationContextProxy.getBean(BuildingToModelConverter.class).convert(building, game.getGameId()));

        applicationContextProxy.getBean(AllocationRemovalService.class)
            .removeAllocationsAndReservations(syncCache, planet, construction.getConstructionId());

        status = ProcessStatus.READY_TO_DELETE;

        syncCache.saveGameItem(toModel());
    }

    @Override
    public void cleanup(SyncCache syncCache) {
        throw new UnsupportedOperationException();
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

    //TODO extract
    private void finishConstruction(SyncCache syncCache) {
        log.info("Finishing construction...");

        applicationContextProxy.getBean(AllocationRemovalService.class)
            .removeAllocationsAndReservations(syncCache, planet, construction.getConstructionId());

        building.setLevel(building.getLevel() + 1);
        building.setConstruction(null);

        log.info("Upgraded building: {}", building);

        syncCache.deleteGameItem(construction.getConstructionId(), GameItemType.CONSTRUCTION);
        syncCache.saveGameItem(applicationContextProxy.getBean(BuildingToModelConverter.class).convert(building, game.getGameId()));

        syncCache.addMessage(
            planet.getOwner(),
            WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED,
            construction.getConstructionId(),
            () -> applicationContextProxy.getBean(WsMessageSender.class).planetQueueItemDeleted(planet.getOwner(), planet.getPlanetId(), construction.getConstructionId())
        );

        Surface surface = planet.findSurfaceByBuildingIdValidated(building.getBuildingId());
        syncCache.addMessage(
            planet.getOwner(),
            WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED,
            surface.getSurfaceId(),
            () -> applicationContextProxy.getBean(WsMessageSender.class)
                .planetSurfaceModified(
                    planet.getOwner(),
                    planet.getPlanetId(),
                    applicationContextProxy.getBean(SurfaceToResponseConverter.class)
                        .convert(surface)
                )
        );
    }

    @Override
    public String toString() {
        return String.format("%s(processId=%s, status=%s)", getClass().getSimpleName(), processId, status);
    }
}
