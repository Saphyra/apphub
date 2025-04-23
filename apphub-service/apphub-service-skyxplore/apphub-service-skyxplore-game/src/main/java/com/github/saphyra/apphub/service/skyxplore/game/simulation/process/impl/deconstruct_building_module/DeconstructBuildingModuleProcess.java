package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruct_building_module;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
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
public class DeconstructBuildingModuleProcess implements Process {
    //Own fields
    @Getter
    @NonNull
    private final UUID processId;

    @NonNull
    private final UUID deconstructionId;

    @Getter
    @NonNull
    private volatile ProcessStatus status;

    @NonNull
    private final UUID location;

    //External fields
    @NonNull
    private final Game game;

    @NonNull
    private final GameData gameData;

    @NonNull
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public ProcessType getType() {
        return ProcessType.DECONSTRUCT_BUILDING_MODULE;
    }

    @Override
    public UUID getExternalReference() {
        return deconstructionId;
    }

    @Override
    public int getPriority() {
        return gameData.getPriorities().findByLocationAndType(location, PriorityType.CONSTRUCTION).getValue() * findDeconstruction().getPriority() * GameConstants.PROCESS_PRIORITY_MULTIPLIER;
    }

    private Deconstruction findDeconstruction() {
        return gameData.getDeconstructions()
            .findByDeconstructionIdValidated(deconstructionId);
    }

    @Override
    public void work() {
        log.info("Working on {}", this);

        DeconstructBuildingModuleProcessConditions conditions = applicationContextProxy.getBean(DeconstructBuildingModuleProcessConditions.class);
        DeconstructBuildingModuleProcessHelper helper = applicationContextProxy.getBean(DeconstructBuildingModuleProcessHelper.class);

        if (status == ProcessStatus.CREATED) {
            status = ProcessStatus.IN_PROGRESS;
        }

        GameProgressDiff progressDiff = game.getProgressDiff();
        if (!conditions.hasWorkProcess(gameData, processId)) {
            helper.startWork(progressDiff, gameData, processId, deconstructionId, location);
        }

        if (!conditions.workFinished(gameData, processId)) {
            log.info("Work in progress...");
            return;
        }

        helper.finishDeconstruction(progressDiff, gameData, deconstructionId);
        status = ProcessStatus.READY_TO_DELETE;
    }

    @Override
    public void cleanup() {
        gameData.getProcesses()
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
