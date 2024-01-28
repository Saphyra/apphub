package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.StringStringMap;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import static java.util.Objects.isNull;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
public class WorkProcess implements Process {
    @Getter
    @NonNull
    private final UUID processId;

    @Getter
    @NonNull
    private volatile ProcessStatus status;

    @Getter
    @NonNull
    private final UUID externalReference;

    private final String buildingDataId;

    @NonNull
    private final SkillType skillType;

    @NonNull
    private final Integer requiredWorkPoints;

    @NonNull
    private volatile Integer completedWorkPoints;

    @NonNull
    private final WorkProcessType workProcessType;

    private final UUID targetId;

    @NonNull
    private final GameData gameData;

    @NonNull
    private final UUID location;

    @NonNull
    private final ApplicationContextProxy applicationContextProxy;

    @NonNull
    private final Game game;

    @Override
    public int getPriority() {
        return gameData.getProcesses()
            .findByIdValidated(externalReference)
            .getPriority() + 1;
    }

    @Override
    public ProcessType getType() {
        return ProcessType.WORK;
    }

    @SneakyThrows
    @Override
    public void work() {
        log.info("Working on {}", this);

        WorkProcessHelper helper = applicationContextProxy.getBean(WorkProcessHelper.class);
        WorkProcessConditions conditions = applicationContextProxy.getBean(WorkProcessConditions.class);
        GameProgressDiff progressDiff = game.getProgressDiff();

        if (status == ProcessStatus.CREATED) {
            if (isNull(buildingDataId)) {
                helper.allocateParentAsBuildingIfPossible(progressDiff, gameData, processId, externalReference);
            } else {
                helper.allocateBuildingIfPossible(progressDiff, gameData, processId, location, buildingDataId);
            }

            if (conditions.hasBuildingAllocated(gameData, processId)) {
                status = ProcessStatus.IN_PROGRESS;
            }
        }

        if (status != ProcessStatus.IN_PROGRESS) {
            log.info("Waiting for initialization");
            return;
        }

        if (!conditions.hasCitizenAllocated(gameData, processId)) {
            log.info("Citizen is not allocated to work {}. Hiring one...", processId);

            helper.allocateCitizenIfPossible(progressDiff, gameData, processId, location, skillType, requiredWorkPoints);

            if (!conditions.hasCitizenAllocated(gameData, processId)) {
                log.info("Failed allocating citizen to work {}", processId);
                return;
            }
        }

        int workPointsLeft = requiredWorkPoints - completedWorkPoints;

        int finishedWork = helper.work(progressDiff, gameData, processId, skillType, workPointsLeft, workProcessType, targetId);
        completedWorkPoints += finishedWork;

        if (completedWorkPoints >= requiredWorkPoints) {
            helper.releaseBuildingAndCitizen(progressDiff, gameData, processId);
            status = ProcessStatus.DONE;
        }
    }

    @Override
    public void cleanup() {
        GameProgressDiff progressDiff = game.getProgressDiff();

        applicationContextProxy.getBean(WorkProcessHelper.class)
            .releaseBuildingAndCitizen(progressDiff, gameData, processId);

        status = ProcessStatus.READY_TO_DELETE;

        progressDiff.save(toModel());
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
                new BiWrapper<>(ProcessParamKeys.WORK_PROCESS_TYPE, workProcessType.name()),
                new BiWrapper<>(ProcessParamKeys.TARGET_ID, applicationContextProxy.getBean(UuidConverter.class).convertDomain(targetId)),
                new BiWrapper<>(ProcessParamKeys.COMPLETED_WORK_POINTS, String.valueOf(completedWorkPoints))
            ))
        );
        return model;
    }

    @Override
    public String toString() {
        return String.format(
            "%s(processId=%s, status=%s, requiredWorkPoints=%s, completedWorkPoints=%s)",
            getClass().getSimpleName(),
            processId,
            status,
            requiredWorkPoints,
            completedWorkPoints
        );
    }
}
