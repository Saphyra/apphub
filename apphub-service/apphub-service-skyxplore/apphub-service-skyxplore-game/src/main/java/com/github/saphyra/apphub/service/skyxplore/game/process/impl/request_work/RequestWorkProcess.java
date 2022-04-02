package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessParamKeys;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItemToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.construction.BuildingConstructionToQueueItemConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ConstructionToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.PlanetToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Future;

import static java.util.Objects.isNull;

//TODO unit test
@Slf4j
public class RequestWorkProcess implements Process {
    @Getter
    private final UUID processId;

    @Getter
    private volatile ProcessStatus status;

    @Getter
    private final UUID externalReference;

    private final String buildingDataId;
    private final SkillType skillType;
    private final int requiredWorkPoints;
    private final RequestWorkProcessType requestWorkProcessType;
    private final UUID targetId;

    private volatile Future<ExecutionResult<Work>> workFuture;
    private volatile int completedWorkPoints = 0;
    private volatile int cycle;

    private final Game game;
    private final Planet planet;

    private final ApplicationContextProxy applicationContextProxy;

    public RequestWorkProcess(ApplicationContextProxy applicationContextProxy, UUID externalReference, Game game, Planet planet, String buildingDataId, SkillType skillType, int requiredWorkPoints) {
        this(applicationContextProxy, externalReference, game, planet, buildingDataId, skillType, requiredWorkPoints, RequestWorkProcessType.OTHER, null);
    }

    //TODO move to factory
    public RequestWorkProcess(
        ApplicationContextProxy applicationContextProxy,
        UUID externalReference,
        Game game,
        Planet planet,
        String buildingDataId,
        SkillType skillType,
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
        this.skillType = skillType;
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
                int completedWorkPoints = workFuture.get()
                    .getOrThrow()
                    .getWorkPoints();
                this.completedWorkPoints += completedWorkPoints;

                updateTarget(syncCache, completedWorkPoints);

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
                maybeWorker = applicationContextProxy.getBean(CitizenFinder.class).getSuitableCitizen(planet, skillType);
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
                .skillType(skillType)
                .applicationContextProxy(applicationContextProxy)
                .build();
            workFuture = applicationContextProxy.getBean(ExecutorServiceBean.class).asyncProcess(work);
        }

        if (requiredWorkPoints == completedWorkPoints) {
            status = ProcessStatus.DONE;
        }
    }

    //TODO extract
    private void updateTarget(SyncCache syncCache, int completedWorkPoints) {
        switch (requestWorkProcessType) {
            case CONSTRUCTION:
                Surface surface = planet.getSurfaces()
                    .values()
                    .stream()
                    .filter(s -> !isNull(s.getBuilding()))
                    .filter(s -> !isNull(s.getBuilding().getConstruction()))
                    .filter(s -> s.getBuilding().getConstruction().getConstructionId().equals(targetId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Target not found with id " + targetId + " for type " + requestWorkProcessType + " on planet " + planet.getPlanetId() + " in game " + game.getGameId()));

                Building building = surface.getBuilding();
                Construction construction = building.getConstruction();
                construction.setCurrentWorkPoints(construction.getCurrentWorkPoints() + completedWorkPoints);

                syncCache.saveGameItem(applicationContextProxy.getBean(ConstructionToModelConverter.class).convert(construction, game.getGameId()));

                WsMessageSender messageSender = applicationContextProxy.getBean(WsMessageSender.class);
                syncCache.addMessage(
                    planet.getOwner(),
                    WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED,
                    surface.getSurfaceId(),
                    () -> messageSender.planetSurfaceModified(planet.getOwner(), planet.getPlanetId(), applicationContextProxy.getBean(SurfaceToResponseConverter.class).convert(surface))
                );

                syncCache.addMessage(
                    planet.getOwner(),
                    WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_MODIFIED,
                    construction.getConstructionId(),
                    () -> messageSender.planetQueueItemModified(
                        planet.getOwner(),
                        planet.getPlanetId(),
                        applicationContextProxy.getBean(QueueItemToResponseConverter.class).convert(applicationContextProxy.getBean(BuildingConstructionToQueueItemConverter.class).convert(surface.getBuilding()), planet)
                    )
                );
                break;
            case OTHER:
                break;
            default:
                log.warn("No handler for requestWorkProcessType {}", requestWorkProcessType);
                break;
        }
    }

    @Override
    public void cancel(SyncCache syncCache) {
        if (!isNull(workFuture)) {
            workFuture.cancel(true);
        }
        cleanup(syncCache);
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
        ProcessModel model = new ProcessModel();
        ProcessModel result = new ProcessModel();
        result.setId(processId);
        result.setGameId(game.getGameId());
        result.setType(GameItemType.PROCESS);
        result.setProcessType(getType());
        result.setLocation(planet.getPlanetId());
        result.setLocationType(LocationType.PLANET.name());
        result.setExternalReference(getExternalReference());
        result.setData(CollectionUtils.toMap(
            new BiWrapper<>(ProcessParamKeys.BUILDING_DATA_ID, buildingDataId),
            new BiWrapper<>(ProcessParamKeys.SKILL_TYPE, skillType.name()),
            new BiWrapper<>(ProcessParamKeys.REQUIRED_WORK_POINTS, String.valueOf(requiredWorkPoints)),
            new BiWrapper<>(ProcessParamKeys.REQUEST_WORK_PROCESS_TYPE, requestWorkProcessType.name()),
            new BiWrapper<>(ProcessParamKeys.TARGET_ID, applicationContextProxy.getBean(UuidConverter.class).convertDomain(targetId)),
            new BiWrapper<>(ProcessParamKeys.COMPLETED_WORK_POINTS, String.valueOf(completedWorkPoints)),
            new BiWrapper<>(ProcessParamKeys.CYCLE, String.valueOf(cycle))
        ));
        return model;
    }
}
