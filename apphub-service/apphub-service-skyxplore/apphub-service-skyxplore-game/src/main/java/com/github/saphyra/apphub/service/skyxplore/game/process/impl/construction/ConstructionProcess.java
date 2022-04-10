package com.github.saphyra.apphub.service.skyxplore.game.process.impl.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
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
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.UseAllocatedResourceService;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcess;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
@Slf4j
public class ConstructionProcess implements Process {
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
    private final Building building;
    @NonNull
    private final Construction construction;
    @NonNull
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

        List<Process> productionOrderProcesses = game.getProcesses()
            .getByExternalReferenceAndType(processId, ProcessType.PRODUCTION_ORDER);
        if (productionOrderProcesses.stream().anyMatch(process -> process.getStatus() != ProcessStatus.DONE)) {
            log.info("Waiting for ProductionOrderProcesses to finish...");
            return;
        }

        List<Process> workProcesses = game.getProcesses().getByExternalReferenceAndType(processId, ProcessType.REQUEST_WORK);
        if (workProcesses.isEmpty()) {
            createRequestWorkProcesses(syncCache);
        } else if (workProcesses.stream().allMatch(process -> process.getStatus() == ProcessStatus.DONE)) {
            applicationContextProxy.getBean(FinishConstructionService.class)
                .finishConstruction(syncCache, game, planet, building);

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

    private void createRequestWorkProcesses(SyncCache syncCache) {
        applicationContextProxy.getBean(UseAllocatedResourceService.class)
            .resolveAllocations(syncCache, game.getGameId(), planet, construction.getConstructionId());
        List<RequestWorkProcess> requestWorkProcesses = applicationContextProxy.getBean(RequestWorkProcessFactoryForConstruction.class)
            .createRequestWorkProcesses(processId, game, planet, building);

        game.getProcesses()
            .addAll(requestWorkProcesses);
        requestWorkProcesses.stream()
            .map(RequestWorkProcess::toModel)
            .forEach(syncCache::saveGameItem);
    }


    private void createProductionOrderProcesses(SyncCache syncCache) {
        log.info("Creating ProductionOrderProcesses...");

        applicationContextProxy.getBean(ProductionOrderProcessFactoryForConstruction.class)
            .createProductionOrderProcesses(processId, game, planet, construction)
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

    @Override
    public String toString() {
        return String.format("%s(processId=%s, status=%s)", getClass().getSimpleName(), processId, status);
    }
}
