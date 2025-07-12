package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.convoy_movement;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
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
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.UUID;

/**
 * Similar to WorkProcess, makes citizens earn workPoints
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(access = AccessLevel.PACKAGE)
@Slf4j
public class ConvoyMovementProcess implements Process {
    @Getter
    @NonNull
    private final UUID processId;

    @Getter
    @NonNull
    private volatile ProcessStatus status;

    @NonNull
    private final UUID citizenId;
    private final int requiredWorkPoints;
    private int completedWorkPoints;

    @Getter
    @NonNull
    private final UUID externalReference;
    @NonNull
    private final UUID location;
    @NonNull
    private final ApplicationContextProxy applicationContextProxy;
    @NonNull
    private final Game game;

    @Override
    public ProcessType getType() {
        return ProcessType.CONVOY_MOVEMENT;
    }

    @Override
    public int getPriority() {
        return game.getData()
            .getProcesses()
            .findByIdValidated(externalReference)
            .getPriority() + 1;
    }

    /**
     * <ol>
     *     <li>Calculates remaining workPoints</li>
     *     <li>Calculates how much workPoints can be earned per tick</li>
     *     <li>Makes the citizen work</li>
     *     <li>Wait until all the workPoints are earned</li>
     * </ol>
     */
    @Override
    public void work() {
        if (status == ProcessStatus.CREATED) {
            status = ProcessStatus.IN_PROGRESS;
        }

        ConvoyMovementProcessHelper helper = applicationContextProxy.getBean(ConvoyMovementProcessHelper.class);

        if (status == ProcessStatus.IN_PROGRESS) {
            GameData gameData = game.getData();

            int missingWorkPoints = requiredWorkPoints - completedWorkPoints;
            int workToBeDone = helper.getWorkPointsPerTick(gameData, citizenId, missingWorkPoints);
            log.info("Missing workPoints: {} - Citizen can work {} this tick.", missingWorkPoints, workToBeDone);
            helper.work(game.getProgressDiff(), gameData, citizenId, SkillType.LOGISTICS, workToBeDone);

            completedWorkPoints += workToBeDone;
            log.info("{} workPoints completed out of {}", completedWorkPoints, requiredWorkPoints);
        }

        if (completedWorkPoints >= requiredWorkPoints) {
            log.info("Convoy movement is finished.");
            status = ProcessStatus.DONE;
        }
    }

    @Override
    public void cleanup() {
        GameProgressDiff progressDiff = game.getProgressDiff();

        status = ProcessStatus.READY_TO_DELETE;

        progressDiff.save(toModel());
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
        model.setLocation(location);
        model.setExternalReference(getExternalReference());
        model.setData(Map.of(
            ProcessParamKeys.CITIZEN_ID, uuidConverter.convertDomain(citizenId),
            ProcessParamKeys.REQUIRED_WORK_POINTS, String.valueOf(requiredWorkPoints),
            ProcessParamKeys.COMPLETED_WORK_POINTS, String.valueOf(completedWorkPoints)
        ));

        return model;
    }

    @Override
    public String toString() {
        return String.format(
            "%s(processId=%s, status=%s, externalReference=%s, citizenId=%s, requiredWorkPoints=%s, completedWorkPoints=%s)",
            getClass().getSimpleName(),
            processId,
            status,
            externalReference,
            citizenId,
            requiredWorkPoints,
            completedWorkPoints
        );
    }
}
