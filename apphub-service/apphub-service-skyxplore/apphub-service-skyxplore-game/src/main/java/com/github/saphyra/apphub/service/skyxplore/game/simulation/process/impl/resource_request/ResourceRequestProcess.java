package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.resource_request;

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
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
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
public class ResourceRequestProcess implements Process {
    //Own fields
    @Getter
    @NonNull
    private final UUID processId;

    @Getter
    @NonNull
    private volatile ProcessStatus status;

    @NonNull
    private final UUID location;

    @Getter
    @NonNull
    private final UUID externalReference;

    @NonNull
    private final UUID reservedStorageId;

    //External fields
    @NonNull
    private final Game game;

    @NonNull
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public ProcessType getType() {
        return ProcessType.RESOURCE_REQUEST;
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
        if (status == ProcessStatus.CREATED) {
            status = ProcessStatus.IN_PROGRESS;
        }

        GameData gameData = game.getData();
        ResourceRequestProcessHelper helper = applicationContextProxy.getBean(ResourceRequestProcessHelper.class);

        MissingResources missingResources = helper.getMissingResources(gameData, reservedStorageId);
        if (missingResources.getToDeliver() > 0) {
            log.info("Resources to deliver: {}", missingResources);
            int delivered = helper.initiateDelivery(game, processId, location, reservedStorageId, missingResources.getToDeliver());
            log.info("Delivery initiated for {} resources.", delivered);
            missingResources.decreaseToRequest(delivered);
        }

        if (missingResources.getToRequest() > 0) {
            log.info("{} resources are still missing. Requesting production...", missingResources);
            helper.createProductionRequest(game, location, processId, reservedStorageId, missingResources.getToRequest());
        }

        if (gameData.getProcesses().getByExternalReference(processId).stream().allMatch(process -> process.getStatus() == ProcessStatus.DONE)) {
            status = ProcessStatus.DONE;
        }
    }

    @Override
    public void cleanup() {
        log.info("Cleaning up {}", this);

        GameProgressDiff progressDiff = game.getProgressDiff();

        game.getData()
            .getProcesses()
            .getByExternalReference(processId)
            .forEach(Process::cleanup);

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
                new BiWrapper<>(ProcessParamKeys.RESERVED_STORAGE_ID, uuidConverter.convertDomain(reservedStorageId))
            ))
        );
        return model;
    }

    @Override
    public String toString() {
        return String.format(
            "%s(processId=%s, status=%s, externalReference=%s, reservedStorageId=%s)",
            getClass().getSimpleName(),
            processId,
            status,
            externalReference,
            reservedStorageId
        );
    }
}
