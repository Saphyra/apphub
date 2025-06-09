package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.StringStringMap;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
import jakarta.annotation.Nullable;
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
//TODO unit test
public class ProductionOrderProcess implements Process {
    @Getter
    @NonNull
    private final UUID processId;

    @Getter
    @NonNull
    private volatile ProcessStatus status;

    @Getter
    @Nullable
    private UUID productionOrderId;

    @Getter
    @NonNull
    private final UUID externalReference;
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
        return ProcessType.PRODUCTION_ORDER;
    }

    @Override
    public void work() {
        log.info("Working on {}", this);

        ProductionOrderProcessHelper helper = applicationContextProxy.getBean(ProductionOrderProcessHelper.class);

        if (status == ProcessStatus.CREATED) {
            helper.orderResources(game, location, processId, productionOrderId);
        }

        ProductionOrderProcessConditions conditions = applicationContextProxy.getBean(ProductionOrderProcessConditions.class);

        if (conditions.productionNeeded(gameData, productionOrderId)) {
            helper.tryProduce(game, location, processId, productionOrderId);
        }

        if (conditions.isFinished(gameData, processId, productionOrderId)) {
            status = ProcessStatus.DONE;
        }
    }

    @Override
    public void cleanup() {
        log.info("Cleaning up {}", this);

        GameProgressDiff progressDiff = game.getProgressDiff();

        applicationContextProxy.getBean(AllocationRemovalService.class)
            .removeAllocationsAndReservations(progressDiff, gameData, processId);

        gameData.getProcesses()
            .getByExternalReference(processId)
            .forEach(Process::cleanup);

        gameData.getProductionOrders()
            .findById(productionOrderId)
            .ifPresent(productionOrder -> {
                gameData.getProductionOrders()
                    .remove(productionOrder);
                progressDiff.delete(productionOrderId, GameItemType.PRODUCTION_ORDER);
            });

        status = ProcessStatus.READY_TO_DELETE;

        progressDiff.save(toModel());
    }

    @Override
    public ProcessModel toModel() {
        UuidConverter uuidConverter = applicationContextProxy.getBean(UuidConverter.class);

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
                new BiWrapper<>(ProcessParamKeys.PRODUCTION_ORDER_ID, uuidConverter.convertDomain(productionOrderId))
            ))
        );
        return model;
    }

    @Override
    public String toString() {
        return String.format(
            "%s(processId=%s, status=%s, productionOrderId=%s)",
            getClass().getSimpleName(),
            processId,
            status,
            productionOrderId
        );
    }
}
