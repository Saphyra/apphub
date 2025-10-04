package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.resource_delivery;

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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;

/**
 * Responsible for transporting the required resources to the target
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(access = AccessLevel.PACKAGE)
@Slf4j
public class ResourceDeliveryProcess implements Process {
    @Getter
    @NonNull
    private final UUID processId;

    @Getter
    @NonNull
    private volatile ProcessStatus status;

    @NonNull
    private final UUID resourceDeliveryRequestId;

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
        return ProcessType.RESOURCE_DELIVERY;
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
     *     <li>Calculates how much resources are left to deliver (in case one convoy has not enough capacity to transport everything at once)</li>
     *     <li>Creates a convoy by assigning a citizen to the delivery</li>
     *     <li>Waits until all the required resources are delivered to the destination</li>
     * </ol>
     */
    @Override
    public void work() {
        if (status == ProcessStatus.CREATED) {
            status = ProcessStatus.IN_PROGRESS;
        }

        ResourceDeliveryProcessHelper helper = applicationContextProxy.getBean(ResourceDeliveryProcessHelper.class);

        GameData gameData = game.getData();
        int toDeliver = helper.calculateToDeliver(gameData, resourceDeliveryRequestId);
        log.info("toDeliver before convoy assembly: {}", toDeliver);
        while (toDeliver > 0) {
            log.info("New convoy required. Assembling one.");
            Optional<Integer> convoyCapacity = helper.assembleConvoy(game, location, processId, resourceDeliveryRequestId, toDeliver);
            log.info("Convoy assembled with capacity: {}", convoyCapacity);
            if (convoyCapacity.isPresent()) {
                toDeliver -= convoyCapacity.get();
            } else {
                log.info("Convoy could not be assembled.");
                break;
            }

            log.info("{} convoy capacity left.", toDeliver);
        }

        log.info("toDeliver after convoyAssembly: {}", toDeliver);
        if (toDeliver <= 0 && gameData.getProcesses().getByExternalReference(processId).stream().allMatch(process -> process.getStatus() == ProcessStatus.DONE)) {
            status = ProcessStatus.DONE;
        }
    }

    @Override
    public void cleanup() {
        log.info("Cleaning up {}", this);

        GameProgressDiff progressDiff = game.getProgressDiff();

        GameData gameData = game.getData();
        applicationContextProxy.getBean(AllocationRemovalService.class)
            .removeAllocationsAndReservations(progressDiff, gameData, resourceDeliveryRequestId);

        gameData.getProcesses()
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
                new BiWrapper<>(ProcessParamKeys.RESOURCE_DELIVERY_REQUEST_ID, uuidConverter.convertDomain(resourceDeliveryRequestId))
            ))
        );
        return model;
    }

    @Override
    public String toString() {
        return String.format(
            "%s(processId=%s, status=%s, externalReference=%s, resourceDeliveryRequestId=%s)",
            getClass().getSimpleName(),
            processId,
            status,
            externalReference,
            resourceDeliveryRequestId
        );
    }
}
