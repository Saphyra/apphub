package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.StringStringMap;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocationToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocationToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessParamKeys;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.update_target.UpdateTargetService;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.work.Work;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.BuildingAllocationFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.CitizenAllocationFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Future;

import static java.util.Objects.isNull;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
//TODO unit test
public class RequestWorkProcess implements Process {
    @Getter
    @NonNull
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
    private volatile int completedWorkPoints;
    private volatile int cycle;

    private final GameData gameData;
    private final UUID location;

    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public int getPriority() {
        return gameData.getProcesses()
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
        log.info("Working on {}", this);

        if (status == ProcessStatus.CREATED) {
            status = ProcessStatus.IN_PROGRESS;
        }

        if (!isNull(buildingDataId) && gameData.getBuildingAllocations().findByProcessId(processId).isEmpty()) {
            if (allocateBuildingIfPossible(syncCache)) {
                return;
            }
        }

        if (!isNull(workFuture)) {
            log.info("Citizen is already working.");
            if (workFuture.isDone()) {
                finishWork(syncCache);

                workFuture = null;

                cycle++;
                log.info("Cycle: {}", cycle);
                if (cycle >= 10) {
                    releaseBuildingAndCitizen(syncCache);
                    cycle = 0;
                    return;
                }
            }
        }

        if (requiredWorkPoints <= completedWorkPoints) {
            log.info("{} finished.", this);
            releaseBuildingAndCitizen(syncCache);
            status = ProcessStatus.DONE;
        }

        if (isNull(workFuture) && status == ProcessStatus.IN_PROGRESS) {
            startWorkIfPossible(syncCache);
        }
    }

    private void startWorkIfPossible(SyncCache syncCache) {
        Optional<UUID> maybeWorker = gameData.getCitizenAllocations()
            .findByProcessId(processId)
            .map(CitizenAllocation::getCitizenId);
        log.info("Scheduling next cycle of work. Currently employed citizen: {}", maybeWorker);

        if (maybeWorker.isPresent()) {
            UUID worker = maybeWorker.get();
            if (gameData.getCitizens().findByCitizenIdValidated(worker).getMorale() < applicationContextProxy.getBean(GameProperties.class).getCitizen().getWorkPointsPerSeconds()) {
                log.info("Citizen {} has not enough morale for the next work cycle.", worker);
                releaseCitizen(syncCache);
                maybeWorker = Optional.empty();
            }
        }

        if (maybeWorker.isEmpty()) {
            maybeWorker = applicationContextProxy.getBean(CitizenFinder.class)
                .getSuitableCitizen(gameData, location, skillType);
            log.info("Suitable citizen found for work: {}", maybeWorker);
        }

        if (maybeWorker.isEmpty()) {
            log.info("No suitable worker found.");
            return;
        }

        UUID worker = maybeWorker.get();

        CitizenAllocation citizenAllocation = applicationContextProxy.getBean(CitizenAllocationFactory.class)
            .create(worker, processId);
        gameData.getCitizenAllocations()
            .add(citizenAllocation);
        CitizenAllocationModel citizenAllocationModel = applicationContextProxy.getBean(CitizenAllocationToModelConverter.class)
            .convert(gameData.getGameId(), citizenAllocation);
        syncCache.saveGameItem(citizenAllocationModel);

        int workPointsLeft = requiredWorkPoints - completedWorkPoints;
        int workPointsPerSeconds = applicationContextProxy.getBean(GameProperties.class).getCitizen().getWorkPointsPerSeconds();
        log.info("WorkPointsLeft: {}, workPointsPerSeconds: {}", workPointsLeft, workPointsPerSeconds);
        Work work = Work.builder()
            .workPoints(Math.min(workPointsLeft, workPointsPerSeconds))
            .game(applicationContextProxy.getBean(GameDao.class).findById(gameData.getGameId()))
            .location(location)
            .citizenId(worker)
            .skillType(skillType)
            .applicationContextProxy(applicationContextProxy)
            .build(); //TODO save work to database
        log.info("{} created", work);
        workFuture = applicationContextProxy.getBean(ExecutorServiceBean.class)
            .asyncProcess(work);
    }

    private void finishWork(SyncCache syncCache) throws Exception {
        int completedWorkPoints = workFuture.get()
            .getOrThrow()
            .getWorkPoints();

        this.completedWorkPoints += completedWorkPoints;
        log.info("Citizen completed {} workPoints. Total: {}. Required: {}", completedWorkPoints, this.completedWorkPoints, requiredWorkPoints);

        applicationContextProxy.getBean(UpdateTargetService.class)
            .updateTarget(syncCache, requestWorkProcessType, gameData, location, targetId, completedWorkPoints);
    }

    private boolean allocateBuildingIfPossible(SyncCache syncCache) {
        Optional<UUID> maybeSuitableBuilding = applicationContextProxy.getBean(ProductionBuildingFinder.class)
            .findSuitableProductionBuilding(gameData, location, buildingDataId);
        log.info("{} is required for work. Found: {}", buildingDataId, maybeSuitableBuilding);

        if (maybeSuitableBuilding.isEmpty()) {
            log.info("No suitable building found.");
            return true;
        }

        UUID buildingId = maybeSuitableBuilding.get();
        BuildingAllocation buildingAllocation = applicationContextProxy.getBean(BuildingAllocationFactory.class)
            .create(buildingId, processId);
        gameData.getBuildingAllocations()
            .add(buildingAllocation);

        BuildingAllocationModel buildingAllocationModel = applicationContextProxy.getBean(BuildingAllocationToModelConverter.class)
            .convert(gameData.getGameId(), buildingAllocation);
        syncCache.saveGameItem(buildingAllocationModel);

        return false;
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

    private void releaseCitizen(SyncCache syncCache) {
        log.info("Releasing citizen and building allocations...");

        gameData.getCitizenAllocations()
            .findByProcessId(processId)
            .ifPresent(citizenAllocation -> {
                gameData.getCitizenAllocations()
                    .remove(citizenAllocation);

                syncCache.deleteGameItem(citizenAllocation.getCitizenAllocationId(), GameItemType.CITIZEN_ALLOCATION);
            });
    }

    private void releaseBuildingAndCitizen(SyncCache syncCache) {
        log.info("Releasing citizen and building allocations...");
        gameData.getBuildingAllocations()
            .findByProcessId(processId)
            .ifPresent(allocation -> {
                gameData.getBuildingAllocations()
                    .remove(allocation);

                syncCache.deleteGameItem(allocation.getBuildingAllocationId(), GameItemType.BUILDING_ALLOCATION);
            });

        releaseCitizen(syncCache);
    }

    @Override
    public ProcessModel toModel() {
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
                new BiWrapper<>(ProcessParamKeys.BUILDING_DATA_ID, buildingDataId),
                new BiWrapper<>(ProcessParamKeys.SKILL_TYPE, skillType.name()),
                new BiWrapper<>(ProcessParamKeys.REQUIRED_WORK_POINTS, String.valueOf(requiredWorkPoints)),
                new BiWrapper<>(ProcessParamKeys.REQUEST_WORK_PROCESS_TYPE, requestWorkProcessType.name()),
                new BiWrapper<>(ProcessParamKeys.TARGET_ID, applicationContextProxy.getBean(UuidConverter.class).convertDomain(targetId)),
                new BiWrapper<>(ProcessParamKeys.COMPLETED_WORK_POINTS, String.valueOf(completedWorkPoints)),
                new BiWrapper<>(ProcessParamKeys.CYCLE, String.valueOf(cycle))
            ))
        );
        return model;
    }

    @Override
    public String toString() {
        return String.format(
            "%s(processId=%s, status=%s, requiredWorkPoints=%s, completedWorkPoints=%s, cycle=%s, hasFuture=%s)",
            getClass().getSimpleName(),
            processId,
            status,
            requiredWorkPoints,
            completedWorkPoints,
            cycle,
            !isNull(workFuture)
        );
    }
}
