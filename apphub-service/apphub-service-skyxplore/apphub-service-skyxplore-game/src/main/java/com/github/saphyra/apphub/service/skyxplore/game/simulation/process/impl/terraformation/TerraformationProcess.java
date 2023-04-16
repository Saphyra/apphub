package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.terraformation;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order.ProductionOrderService;
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
public class TerraformationProcess implements Process {
    @Getter
    @NonNull
    private final UUID processId;

    @Getter
    @NonNull
    private volatile ProcessStatus status;

    @NonNull
    private final GameData gameData;
    @NonNull
    private final UUID location;

    @NonNull
    private final UUID terraformationId; //constructionId

    @NonNull
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public UUID getExternalReference() {
        return terraformationId;
    }

    private Construction findTerraformation() {
        return gameData.getConstructions()
            .findByConstructionIdValidated(terraformationId);
    }

    @Override
    public int getPriority() {
        return gameData.getPriorities().findByLocationAndType(location, PriorityType.CONSTRUCTION).getValue() * findTerraformation().getPriority() * GameConstants.PROCESS_PRIORITY_MULTIPLIER;
    }

    @Override
    public ProcessType getType() {
        return ProcessType.TERRAFORMATION;
    }

    @Override
    public void work(SyncCache syncCache) {
        log.info("Working on {}", this);

        if (status == ProcessStatus.CREATED) {
            applicationContextProxy.getBean(ProductionOrderService.class)
                .createProductionOrdersForReservedStorages(syncCache, gameData, processId, terraformationId);

            status = ProcessStatus.IN_PROGRESS;
        }

        TerraformationProcessConditions conditions = applicationContextProxy.getBean(TerraformationProcessConditions.class);

        if (!conditions.productionOrdersComplete(gameData, processId)) {
            log.info("Waiting for ProductionOrderProcesses to finish...");
            return;
        }

        TerraformationProcessHelper helper = applicationContextProxy.getBean(TerraformationProcessHelper.class);
        if (!conditions.hasWorkProcesses(gameData, processId)) {
            helper.startWork(syncCache, gameData, processId, terraformationId);
        }

        if (!conditions.workFinished(gameData, processId)) {
            log.info("Waiting for WorkProcesses to finish...");
            return;
        }

        helper.finishTerraformation(syncCache, gameData, terraformationId);
        status = ProcessStatus.READY_TO_DELETE;
    }

    @Override
    public void cancel(SyncCache syncCache) {
        gameData.getProcesses()
            .getByExternalReference(processId)
            .forEach(process -> process.cancel(syncCache));

        status = ProcessStatus.READY_TO_DELETE;

        syncCache.saveGameItem(toModel());
    }

    @Override
    public void cleanup(SyncCache syncCache) {
        gameData.getProcesses()
            .getByExternalReference(processId)
            .forEach(process -> process.cleanup(syncCache));
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
