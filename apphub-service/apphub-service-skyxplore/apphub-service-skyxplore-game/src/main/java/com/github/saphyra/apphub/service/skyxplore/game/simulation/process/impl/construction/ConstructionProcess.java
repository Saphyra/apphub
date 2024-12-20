package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construction;

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

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
@Slf4j
@Deprecated(forRemoval = true)
public class ConstructionProcess implements Process {
    @Getter
    @NonNull
    private final UUID processId;

    @Getter
    @NonNull
    private volatile ProcessStatus status;

    @NonNull
    private final GameData gameData;

    @NonNull
    private final UUID constructionId;

    @NonNull
    private UUID location;

    @NonNull
    private final ApplicationContextProxy applicationContextProxy;

    @NonNull
    private final Game game;

    @Override
    public UUID getExternalReference() {
        return constructionId;
    }

    private Construction findConstruction() {
        return gameData.getConstructions()
            .findByConstructionIdValidated(constructionId);
    }

    @Override
    public int getPriority() {
        return gameData.getPriorities().findByLocationAndType(location, PriorityType.CONSTRUCTION).getValue() * findConstruction().getPriority() * GameConstants.PROCESS_PRIORITY_MULTIPLIER;
    }

    @Override
    public ProcessType getType() {
        return ProcessType.CONSTRUCTION;
    }

    @Override
    public void work() {
        log.info("Working on {}", this);

        ConstructionProcessHelper helper = applicationContextProxy.getBean(ConstructionProcessHelper.class);
        GameProgressDiff progressDiff = game.getProgressDiff();

        if (status == ProcessStatus.CREATED) {
            helper.createProductionOrders(progressDiff, gameData, processId, constructionId);

            status = ProcessStatus.IN_PROGRESS;
        }

        ConstructionProcessConditions conditions = applicationContextProxy.getBean(ConstructionProcessConditions.class);

        if (!conditions.productionOrdersComplete(gameData, processId)) {
            log.info("Waiting for ProductionOrderProcesses to finish...");
            return;
        }

        if (!conditions.hasWorkProcesses(gameData, processId)) {
            helper.startWork(progressDiff, gameData, processId, constructionId);
        }

        if (!conditions.workFinished(gameData, processId)) {
            log.info("Waiting for WorkProcesses to finish...");
            return;
        }

        helper.finishConstruction(progressDiff, gameData, constructionId);
        status = ProcessStatus.READY_TO_DELETE;
    }

    @Override
    public void cleanup() {
        gameData.getProcesses()
            .getByExternalReference(processId)
            .forEach(Process::cleanup);

        status = ProcessStatus.READY_TO_DELETE;

        game.getProgressDiff().save(toModel());
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

        return model;
    }

    @Override
    public String toString() {
        return String.format("%s(processId=%s, status=%s)", getClass().getSimpleName(), processId, status);
    }
}
