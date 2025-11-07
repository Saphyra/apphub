package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.terraformation;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * Handles terraformation of a surface
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
@Slf4j
public class TerraformationProcess implements Process {
    @Getter
    @NonNull
    private final UUID processId;

    @Getter
    @NonNull
    private volatile ProcessStatus status;

    @NonNull
    private final UUID location;

    @NonNull
    private final UUID terraformationId; //constructionId

    @NonNull
    private final ApplicationContextProxy applicationContextProxy;

    @NonNull
    private final Game game;

    @Override
    public UUID getExternalReference() {
        return terraformationId;
    }

    private Construction findTerraformation() {
        return game.getData()
            .getConstructions()
            .findByIdValidated(terraformationId);
    }

    @Override
    public int getPriority() {
        return game.getData()
            .getPriorities()
            .findByLocationAndType(location, PriorityType.CONSTRUCTION)
            .getValue()
            * findTerraformation().getPriority()
            * GameConstants.PROCESS_PRIORITY_MULTIPLIER;
    }

    @Override
    public ProcessType getType() {
        return ProcessType.TERRAFORMATION;
    }

    /**
     * <ol>
     *     <li>Initiates the production and delivery of the necessary resources</li>
     *     <li>Waits until resources are available</li>
     *     <li>Initiates WorkProcesses</li>
     *     <li>Waits until work is done</li>
     *     <li>Finishes the terraformation</li>
     * </ol>
     *
     * ProcessStatus instantly switches to READY_TO_DELETE, since there is no parent process that tracks this one.
     */
    @Override
    public void work() {
        TerraformationProcessHelper helper = applicationContextProxy.getBean(TerraformationProcessHelper.class);

        if (status == ProcessStatus.CREATED) {
            log.info("Creating ResourceRequestProcesses...");
            helper.createResourceRequestProcess(game, location, processId, terraformationId);

            status = ProcessStatus.IN_PROGRESS;
        }

        TerraformationProcessConditions conditions = applicationContextProxy.getBean(TerraformationProcessConditions.class);
        GameData gameData = game.getData();

        if (!conditions.hasWorkProcesses(gameData, processId) && !conditions.resourcesAvailable(gameData, terraformationId)) {
            log.info("Waiting for resources...");
            return;
        }

        if (!conditions.hasWorkProcesses(gameData, processId)) {
            helper.startWork(game, processId, terraformationId);
        }

        if (!conditions.workFinished(gameData, processId)) {
            log.info("Waiting for WorkProcesses to finish...");
            return;
        }

        GameProgressDiff progressDiff = game.getProgressDiff();
        helper.finishConstruction(progressDiff, gameData, terraformationId);
        status = ProcessStatus.READY_TO_DELETE;
    }

    @Override
    public void cleanup() {
        game.getData()
            .getProcesses()
            .getByExternalReference(processId)
            .forEach(Process::cleanup);

        status = ProcessStatus.READY_TO_DELETE;

        game.getProgressDiff()
            .save(toModel());
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

        return model;
    }

    @Override
    public String toString() {
        return String.format("%s(processId=%s, status=%s)", getClass().getSimpleName(), processId, status);
    }
}
