package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.PlanetToModelConverter;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Future;

import static java.util.Objects.isNull;

//TODO unit test
public class RequestWorkProcess implements Process {
    @Getter
    private final UUID processId;

    @Getter
    private volatile ProcessStatus status;

    @Getter
    private final UUID externalReference;

    private final String buildingDataId;
    private final SkillType requiredSkill;
    private final int requiredWorkPoints;
    private final RequestWorkProcessType requestWorkProcessType;
    private final UUID targetId;

    private volatile Future<ExecutionResult<Work>> workFuture;
    private volatile int completedWorkPoints = 0;
    private volatile int cycle;

    private final Game game;
    private final Planet planet;

    private final ApplicationContextProxy applicationContextProxy;

    public RequestWorkProcess(ApplicationContextProxy applicationContextProxy, UUID externalReference, Game game, Planet planet, String buildingDataId, SkillType requiredSkill, int requiredWorkPoints) {
        this(applicationContextProxy, externalReference, game, planet, buildingDataId, requiredSkill, requiredWorkPoints, RequestWorkProcessType.OTHER, null);
    }

    //TODO move to factory
    public RequestWorkProcess(
        ApplicationContextProxy applicationContextProxy,
        UUID externalReference,
        Game game,
        Planet planet,
        String buildingDataId,
        SkillType requiredSkill,
        int requiredWorkPoints,
        RequestWorkProcessType requestWorkProcessType,
        UUID targetId
    ) {
        processId = applicationContextProxy.getBean(IdGenerator.class)
            .randomUuid();
        status = ProcessStatus.IN_PROGRESS;
        this.externalReference = externalReference;
        this.game = game;
        this.planet = planet;
        this.applicationContextProxy = applicationContextProxy;
        this.requiredSkill = requiredSkill;
        this.requiredWorkPoints = requiredWorkPoints;
        this.requestWorkProcessType = requestWorkProcessType;
        this.targetId = targetId;
        this.buildingDataId = buildingDataId;
    }

    @Override
    public int getPriority() {
        return game.getProcesses()
            .findByIdValidated(externalReference)
            .getPriority() + 1;
    }

    @Override
    public ProcessType getType() {
        return ProcessType.REQUEST_WORK;
    }

    @SneakyThrows
    @Override
    public void work(SyncCache syncCache) {
        if (!isNull(buildingDataId) && planet.getBuildingAllocations().findByProcessId(processId).isEmpty()) {
            Optional<UUID> maybeSuitableBuilding = applicationContextProxy.getBean(ProductionBuildingFinder.class)
                .findSuitableProductionBuildings(planet, buildingDataId);

            if (maybeSuitableBuilding.isEmpty()) {
                return;
            }

            UUID buildingId = maybeSuitableBuilding.get();
            planet.getBuildingAllocations().add(buildingId, processId);

            syncCache.saveGameItem(applicationContextProxy.getBean(PlanetToModelConverter.class).convert(planet, game));
        }

        if (!isNull(workFuture)) {
            if (workFuture.isDone()) {
                completedWorkPoints += workFuture.get()
                    .getOrThrow()
                    .getWorkPoints();

                updateTarget();

                workFuture = null;

                cycle++;
                if (cycle >= 10) {
                    releaseBuildingAndCitizen(syncCache);
                    cycle = 0;
                    return;
                }
            }
        }

        if (isNull(workFuture) && completedWorkPoints < requiredWorkPoints) {
            Optional<UUID> maybeWorker = planet.getCitizenAllocations()
                .findByProcessId(processId);

            if (maybeWorker.isEmpty()) {
                maybeWorker = applicationContextProxy.getBean(CitizenFinder.class).getSuitableCitizen(planet, requiredSkill);
            }

            if (maybeWorker.isEmpty()) {
                return;
            }

            UUID worker = maybeWorker.get();
            planet.getCitizenAllocations()
                .put(worker, processId);

            int workPointsLeft = requiredWorkPoints - completedWorkPoints;
            int workPointsPerSeconds = applicationContextProxy.getBean(GameProperties.class).getCitizen().getWorkPointsPerSeconds();
            Work work = Work.builder()
                .workPoints(Math.min(workPointsLeft, workPointsPerSeconds))
                .game(game)
                .planet(planet)
                .citizenId(worker)
                .applicationContextProxy(applicationContextProxy)
                .build();
            workFuture = applicationContextProxy.getBean(ExecutorServiceBean.class).asyncProcess(work);
        }

        if (requiredWorkPoints == completedWorkPoints) {
            status = ProcessStatus.DONE;
        }
    }

    private void updateTarget() {
        //TODO
    }

    @Override
    public void cancel(SyncCache syncCache) {
        //TODO
    }

    @Override
    public void cleanup(SyncCache syncCache) {
        releaseBuildingAndCitizen(syncCache);

        status = ProcessStatus.READY_TO_DELETE;

        syncCache.saveGameItem(toModel());
    }

    private void releaseBuildingAndCitizen(SyncCache syncCache) {
        planet.getBuildingAllocations().releaseByProcessId(processId);
        planet.getCitizenAllocations().releaseByProcessId(processId);
        syncCache.saveGameItem(applicationContextProxy.getBean(PlanetToModelConverter.class).convert(planet, game));
    }

    @Override
    public ProcessModel toModel() {
        return null; //TODO
    }
}
