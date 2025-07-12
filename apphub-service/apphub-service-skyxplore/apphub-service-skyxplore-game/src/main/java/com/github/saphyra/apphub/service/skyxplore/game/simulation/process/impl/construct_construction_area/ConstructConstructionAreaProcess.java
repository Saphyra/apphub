package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construct_construction_area;

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
 * Handles construction of a ConstructionArea
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
@Slf4j
public class ConstructConstructionAreaProcess implements Process {
    //Own fields
    @Getter
    @NonNull
    private final UUID processId;

    @NonNull
    private final UUID constructionId;

    @Getter
    @NonNull
    private volatile ProcessStatus status;

    @NonNull
    private final UUID location;

    //External fields
    @NonNull
    private final Game game;

    @NonNull
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public ProcessType getType() {
        return ProcessType.CONSTRUCT_CONSTRUCTION_AREA;
    }

    @Override
    public UUID getExternalReference() {
        return constructionId;
    }

    @Override
    public int getPriority() {
        return game.getData()
            .getPriorities()
            .findByLocationAndType(location, PriorityType.CONSTRUCTION).getValue()
            * findConstruction().getPriority()
            * GameConstants.PROCESS_PRIORITY_MULTIPLIER;
    }

    /**
     * <ol>
     *     <li>Initiates the production and delivery of the necessary resources</li>
     *     <li>Waits until resources are available</li>
     *     <li>Initiates WorkProcesses</li>
     *     <li>Waits until work is done</li>
     *     <li>Finishes the construction</li>
     * </ol>
     */
    @Override
    public void work() {
        ConstructConstructionAreaProcessHelper helper = applicationContextProxy.getBean(ConstructConstructionAreaProcessHelper.class);

        if (status == ProcessStatus.CREATED) {
            helper.createResourceRequestProcess(game, location, processId, constructionId);

            status = ProcessStatus.IN_PROGRESS;
        }

        ConstructConstructionAreaProcessConditions conditions = applicationContextProxy.getBean(ConstructConstructionAreaProcessConditions.class);

        GameData gameData = game.getData();
        if (!conditions.resourcesAvailable(gameData, processId, constructionId)) {
            log.info("Waiting for resources...");
            return;
        }

        if (!conditions.hasWorkProcesses(gameData, processId)) {
            helper.startWork(game, processId, constructionId);
        }

        if (!conditions.workFinished(gameData, processId)) {
            log.info("Waiting for WorkProcesses to finish...");
            return;
        }

        GameProgressDiff progressDiff = game.getProgressDiff();
        helper.finishConstruction(progressDiff, gameData, constructionId);
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

    private Construction findConstruction() {
        return game.getData()
            .getConstructions()
            .findByIdValidated(constructionId);
    }

    @Override
    public String toString() {
        return String.format("%s(processId=%s, status=%s)", getClass().getSimpleName(), processId, status);
    }
}
