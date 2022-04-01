package com.github.saphyra.apphub.service.skyxplore.game.process.impl.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
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
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order.ProductionOrderProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcessType;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

//TODO unit test
public class ConstructionProcess implements Process {
    @Getter
    private final UUID processId;

    @Getter
    private volatile ProcessStatus status;

    private final Game game;
    private final Planet planet;
    private final Building building;
    private final Construction construction;

    private final ApplicationContextProxy applicationContextProxy;


    //TODO move to factory
    //TODO call when new construction is started
    public ConstructionProcess(ApplicationContextProxy applicationContextProxy, Game game, Planet planet, Building building) {
        processId = applicationContextProxy.getBean(IdGenerator.class)
            .randomUuid();
        this.applicationContextProxy = applicationContextProxy;
        this.game = game;
        this.planet = planet;
        this.building = building;
        this.construction = building.getConstruction();
        this.status = ProcessStatus.CREATED;
    }

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
        if (status == ProcessStatus.CREATED) {
            createResourceRequirementsProcesses(syncCache);

            status = ProcessStatus.IN_PROGRESS;
        }

        List<Process> productionOrderProcesses = game.getProcesses().getByExternalReferenceAndType(processId, ProcessType.PRODUCTION_ORDER);
        if (productionOrderProcesses.stream().anyMatch(process -> process.getStatus() != ProcessStatus.DONE)) {
            return;
        }

        List<Process> maybeRequestWorkProcess = game.getProcesses().getByExternalReferenceAndType(processId, ProcessType.REQUEST_WORK);
        if (maybeRequestWorkProcess.isEmpty()) {
            createWorkProcesses(syncCache);
        } else if (maybeRequestWorkProcess.stream().allMatch(process -> process.getStatus() == ProcessStatus.DONE)) {
            finishConstruction();
            status = ProcessStatus.DONE;
        }

        if (status == ProcessStatus.DONE) {
            productionOrderProcesses.forEach(process -> process.cleanup(syncCache));
            maybeRequestWorkProcess.forEach(process -> process.cleanup(syncCache));
            status = ProcessStatus.READY_TO_DELETE;
        }
    }

    //TODO extract
    private void createWorkProcesses(SyncCache syncCache) {
        ConstructionRequirements constructionRequirements = applicationContextProxy.getBean(BuildingDataService.class)
            .get(building.getDataId())
            .getConstructionRequirements()
            .get(building.getLevel() + 1);

        int workPointsPerWorker = constructionRequirements.getRequiredWorkPoints() / constructionRequirements.getParallelWorkers();

        for (int i = 0; i < workPointsPerWorker; i++) {
            RequestWorkProcess requestWorkProcess = new RequestWorkProcess(
                applicationContextProxy,
                processId,
                game,
                planet,
                null,
                SkillType.BUILDING,
                workPointsPerWorker,
                RequestWorkProcessType.CONSTRUCTION,
                construction.getConstructionId()
            );
            game.getProcesses()
                .add(requestWorkProcess);
            syncCache.saveGameItem(requestWorkProcess.toModel());
        }
    }

    //TODO extract
    private void createResourceRequirementsProcesses(SyncCache syncCache) {
        planet.getStorageDetails()
            .getReservedStorages()
            .stream()
            .filter(reservedStorage -> reservedStorage.getExternalReference().equals(construction.getConstructionId()))
            .flatMap(reservedStorage -> applicationContextProxy.getBean(ProductionOrderProcessFactory.class).create(applicationContextProxy, processId, game, planet, reservedStorage.getReservedStorageId()).stream())
            .forEach(productionOrderProcess -> {
                game.getProcesses().add(productionOrderProcess);
                syncCache.saveGameItem(productionOrderProcess.toModel());
            });
    }

    @Override
    //TODO call when construction is cancelled
    public void cancel(SyncCache syncCache) {
        //TODO implement
    }

    @Override
    public void cleanup(SyncCache syncCache) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProcessModel toModel() {
        ProcessModel result = new ProcessModel();
        result.setId(processId);
        result.setGameId(game.getGameId());
        result.setType(GameItemType.PROCESS);
        result.setProcessType(getType());
        result.setLocation(planet.getPlanetId());
        result.setLocationType(LocationType.PLANET.name());
        result.setExternalReference(getExternalReference());

        return result;
    }

    //TODO extract
    private void finishConstruction() {
        //TODO delete reservedStorages and allocatedResources
        //TODO remove resources from storage
        //TODO increase building level
    }
}
