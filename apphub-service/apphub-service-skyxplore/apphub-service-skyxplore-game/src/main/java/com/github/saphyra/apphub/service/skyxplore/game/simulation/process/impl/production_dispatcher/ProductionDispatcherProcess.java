package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_dispatcher;

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
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_request.ProductionRequest;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * Assigns the required production requests to eligible ConstructionAreas
 * Eligible ConstructionArea: where a factory of the required resource exists, and there is enough storage available to handle the production.
 * One ConstructionArea can accept only one batch of production of the same source at the same time.
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(access = AccessLevel.PACKAGE)
@Slf4j
public class ProductionDispatcherProcess implements Process {
    @Getter
    @NonNull
    private final UUID processId;

    @Getter
    @NonNull
    private volatile ProcessStatus status;

    @NonNull
    private final UUID productionRequestId;

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
        return ProcessType.PRODUCTION_DISPATCHER;
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
     *     <li>Calculates the amount missing</li>
     *     <li>Looks for ConstructionAreas that can produce the required resources</li>
     *     <li>Assigns productions of found ConstructionAreas</li>
     *     <li>Waits until all the production is dispatched, and finished</li>
     * </ol>
     */
    @Override
    public void work() {
        if (status == ProcessStatus.CREATED) {
            status = ProcessStatus.IN_PROGRESS;
        }

        GameData gameData = game.getData();

        ProductionRequest request = gameData.getProductionRequests()
            .findByIdValidated(productionRequestId);
        int missingAmount = request.getRequestedAmount() - request.getDispatchedAmount();
        log.info("MissingAmount: {}", missingAmount);

        if (missingAmount > 0) {
            int dispatchedAmount = applicationContextProxy.getBean(ProductionDispatcherProcessHelper.class)
                .dispatch(game, location, processId, productionRequestId, missingAmount);
            missingAmount -= dispatchedAmount;
            log.info("{} resources were dispatched to producers. {} still left.", dispatchedAmount, missingAmount);
        }

        if (missingAmount == 0 && gameData.getProcesses().getByExternalReference(processId).stream().allMatch(process -> process.getStatus() == ProcessStatus.DONE)) {
            status = ProcessStatus.DONE;
        }
    }

    @Override
    public void cleanup() {
        log.info("Cleaning up {}", this);

        GameProgressDiff progressDiff = game.getProgressDiff();
        GameData gameData = game.getData();

        gameData.getProcesses()
            .getByExternalReference(processId)
            .forEach(Process::cleanup);

        gameData.getProductionRequests()
            .remove(productionRequestId);
        progressDiff.delete(productionRequestId, GameItemType.PRODUCTION_REQUEST);

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
        model.setData(new StringStringMap(
            CollectionUtils.toMap(
                new BiWrapper<>(ProcessParamKeys.PRODUCTION_REQUEST_ID, uuidConverter.convertDomain(productionRequestId))
            ))
        );
        return model;
    }

    @Override
    public String toString() {
        return String.format(
            "%s(processId=%s, status=%s, externalReference=%s, productionRequestId=%s)",
            getClass().getSimpleName(),
            processId,
            status,
            externalReference,
            productionRequestId
        );
    }
}
