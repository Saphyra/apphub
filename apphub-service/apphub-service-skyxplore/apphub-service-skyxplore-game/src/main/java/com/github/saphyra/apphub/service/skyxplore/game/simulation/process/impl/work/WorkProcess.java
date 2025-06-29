package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.StringStringMap;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
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

    @NonNull
    private final SkillType skillType;

    @NonNull
    private final Integer requiredWorkPoints;

    @NonNull
    @Getter
    private Integer completedWorkPoints;

    @NonNull
    private final UUID location;

    @NonNull
    private final ApplicationContextProxy applicationContextProxy;

    @NonNull
    private final Game game;

    @Override
    public int getPriority() {
        return game.getData()
            .getProcesses()
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
        WorkProcessHelper helper = applicationContextProxy.getBean(WorkProcessHelper.class);
        WorkProcessConditions conditions = applicationContextProxy.getBean(WorkProcessConditions.class);
        GameProgressDiff progressDiff = game.getProgressDiff();
        GameData gameData = game.getData();

        if (status == ProcessStatus.CREATED) {
            if (conditions.canProceed(gameData, externalReference)) {
                if (helper.tryAllocateCitizen(game.getProgressDiff(), gameData, location, processId, skillType)) {
                    status = ProcessStatus.IN_PROGRESS;
                }
            }
        }

        if (status != ProcessStatus.IN_PROGRESS) {
            log.info("Waiting for initialization");
            return;
        }

        int workPointsLeft = requiredWorkPoints - completedWorkPoints;

        int finishedWork = helper.work(progressDiff, gameData, processId, skillType, workPointsLeft);
        completedWorkPoints += finishedWork;
        log.info("{} workPoints were missing, finished {} work, now {} is completed out of {} total.", workPointsLeft, finishedWork, completedWorkPoints, requiredWorkPoints);

        if (completedWorkPoints >= requiredWorkPoints) {
            log.info("Work is finished.");
            helper.releaseCitizen(progressDiff, gameData, processId);
            status = ProcessStatus.DONE;
        }
    }

    @Override
    public void cleanup() {
        GameProgressDiff progressDiff = game.getProgressDiff();

        applicationContextProxy.getBean(WorkProcessHelper.class)
            .releaseCitizen(progressDiff, game.getData(), processId);

        status = ProcessStatus.READY_TO_DELETE;

        progressDiff.save(toModel());
    }

    @Override
    public ProcessModel toModel() {
        ProcessModel model = new ProcessModel();
        model.setId(processId);
        model.setGameId(game.getGameId());
        model.setType(GameItemType.PROCESS);
        model.setProcessType(getType());
        model.setStatus(status);
        model.setLocation(location);
        model.setExternalReference(getExternalReference());
        model.setData(new StringStringMap(
            CollectionUtils.toMap(
                new BiWrapper<>(ProcessParamKeys.SKILL_TYPE, skillType.name()),
                new BiWrapper<>(ProcessParamKeys.REQUIRED_WORK_POINTS, String.valueOf(requiredWorkPoints)),
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
