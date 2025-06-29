package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
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

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(access = AccessLevel.PACKAGE)
@Slf4j
public class ProductionProcess implements Process {
    @Getter
    @NonNull
    private final UUID processId;

    @Getter
    @NonNull
    private volatile ProcessStatus status;

    @NonNull
    private final UUID productionOrderId;
    @NonNull
    private final UUID buildingModuleId;
    private final int amount;

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
        return ProcessType.PRODUCTION;
    }

    @Override
    public int getPriority() {
        return game.getData()
            .getProcesses()
            .findByIdValidated(externalReference)
            .getPriority() + 1;
    }

    @Override
    public void work() {
        ProductionProcessHelper helper = applicationContextProxy.getBean(ProductionProcessHelper.class);

        if (status == ProcessStatus.CREATED) {
            helper.createWorkProcess(game, location, processId, productionOrderId, amount);
            status = ProcessStatus.IN_PROGRESS;
        }

        if (game.getData().getProcesses().getByExternalReference(processId).stream().allMatch(process -> process.getStatus() == ProcessStatus.DONE)) {
            log.info("Production is finished.");
            if (helper.resourcesProduced(game, location, productionOrderId, amount)) {
                log.info("Produced resources successfully stored.");
                releaseBuildingModuleAllocation();
                status = ProcessStatus.DONE;
            }
        } else {
            log.info("Waiting for work to be finished...");
        }
    }

    @Override
    public void cleanup() {
        log.info("Cleaning up {}", this);

        GameProgressDiff progressDiff = game.getProgressDiff();
        GameData gameData = game.getData();

        releaseBuildingModuleAllocation();

        gameData.getProcesses()
            .getByExternalReference(processId)
            .forEach(Process::cleanup);

        status = ProcessStatus.READY_TO_DELETE;

        progressDiff.save(toModel());
    }

    private void releaseBuildingModuleAllocation() {
        GameProgressDiff progressDiff = game.getProgressDiff();
        GameData gameData = game.getData();

        gameData.getBuildingModuleAllocations()
            .findByProcessId(processId)
            .ifPresent(buildingModuleAllocation -> {
                gameData.getBuildingModuleAllocations()
                    .remove(buildingModuleAllocation);
                progressDiff.delete(buildingModuleAllocation.getBuildingModuleAllocationId(), GameItemType.BUILDING_MODULE_ALLOCATION);
                log.info("Allocation for buildingModule {} is deleted.", buildingModuleAllocation.getBuildingModuleId());
            });
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
        model.setData(
            CollectionUtils.toMap(
                new BiWrapper<>(ProcessParamKeys.PRODUCTION_ORDER_ID, uuidConverter.convertDomain(productionOrderId)),
                new BiWrapper<>(ProcessParamKeys.BUILDING_MODULE_ID, uuidConverter.convertDomain(buildingModuleId)),
                new BiWrapper<>(ProcessParamKeys.AMOUNT, String.valueOf(amount))
            )
        );
        return model;
    }

    @Override
    public String toString() {
        return String.format(
            "%s(processId=%s, status=%s, externalReference=%s, productionOrderId=%s, buildingModuleId=%s, amount=%s)",
            getClass().getSimpleName(),
            processId,
            status,
            externalReference,
            productionOrderId,
            buildingModuleId,
            amount
        );
    }
}
