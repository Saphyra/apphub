package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruct_construction_area;

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
public class DeconstructConstructionAreaProcess implements Process {
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
        return ProcessType.DECONSTRUCT_CONSTRUCTION_AREA;
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
            .findByIdValidated(deconstructionId);
    }

    @Override
    public void work() {
        DeconstructConstructionAreaProcessConditions conditions = applicationContextProxy.getBean(DeconstructConstructionAreaProcessConditions.class);
        DeconstructConstructionAreaProcessHelper helper = applicationContextProxy.getBean(DeconstructConstructionAreaProcessHelper.class);
        GameProgressDiff progressDiff = game.getProgressDiff();

        if (status == ProcessStatus.CREATED) {
            helper.initiateDeconstructModules(game, deconstructionId);

            status = ProcessStatus.IN_PROGRESS;
        }

        if (!conditions.modulesDeconstructed(gameData, findDeconstruction().getExternalReference())) {
            log.info("Modules are being deconstructed");
            return;
        }

        if (!conditions.hasWorkProcesses(gameData, processId)) {
            helper.startWork(game, processId, deconstructionId);
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
}
