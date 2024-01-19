package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocationRemovalService;
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
public class StorageSettingProcess implements Process {
    @Getter
    @NonNull
    private final UUID processId;

    @Getter
    @NonNull
    private volatile ProcessStatus status;

    @NonNull
    private final UUID location;

    @NonNull
    private final GameData gameData;
    @NonNull
    private final UUID storageSettingId;
    @NonNull
    @Getter
    private final Integer amount;

    @NonNull
    private final ApplicationContextProxy applicationContextProxy;

    @NonNull
    private final Game game;

    @Override
    public UUID getExternalReference() {
        return storageSettingId;
    }

    private StorageSetting findStorageSetting() {
        return gameData.getStorageSettings()
            .findByStorageSettingIdValidated(storageSettingId);
    }

    @Override
    public int getPriority() {
        return gameData.getPriorities()
            .findByLocationAndType(location, PriorityType.INDUSTRY)
            .getValue()
            * findStorageSetting().getPriority()
            * GameConstants.PROCESS_PRIORITY_MULTIPLIER;
    }

    @Override
    public ProcessType getType() {
        return ProcessType.STORAGE_SETTING;
    }

    @Override
    public void work() {
        log.info("Working on {}", this);

        StorageSettingProcessHelper helper = applicationContextProxy.getBean(StorageSettingProcessHelper.class);

        if (status == ProcessStatus.CREATED) {
            helper.orderResources(game.getProgressDiff(), gameData, processId, findStorageSetting(), amount);

            status = ProcessStatus.IN_PROGRESS;
        }

        StorageSettingProcessConditions conditions = applicationContextProxy.getBean(StorageSettingProcessConditions.class);
        if (conditions.isFinished(gameData, processId)) {
            status = ProcessStatus.READY_TO_DELETE;
        }
    }

    @Override
    public void cleanup() {
        log.info("Cleaning up {}", this);

        gameData.getProcesses()
            .getByExternalReference(processId)
            .forEach(Process::cleanup);

        GameProgressDiff progressDiff = game.getProgressDiff();

        applicationContextProxy.getBean(AllocationRemovalService.class)
            .removeAllocationsAndReservations(progressDiff, gameData, processId);

        status = ProcessStatus.READY_TO_DELETE;

        progressDiff.save(toModel());
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
        model.setData(CollectionUtils.singleValueMap(ProcessParamKeys.AMOUNT, String.valueOf(amount)));
        return model;
    }

    @Override
    public String toString() {
        return String.format(
            "%s(processId=%s, status=%s, storageSettingId=%s)",
            getClass().getSimpleName(),
            processId,
            status,
            storageSettingId
        );
    }
}
