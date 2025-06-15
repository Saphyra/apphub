package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorageFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageSettingProcessFactory implements ProcessFactory {
    private final ApplicationContextProxy applicationContextProxy;
    private final IdGenerator idGenerator;
    private final UuidConverter uuidConverter;
    private final ReservedStorageFactory reservedStorageFactory;

    @Override
    public ProcessType getType() {
        return ProcessType.STORAGE_SETTING;
    }

    @Override
    public StorageSettingProcess createFromModel(Game game, ProcessModel model) {
        return StorageSettingProcess.builder()
            .processId(model.getId())
            .status(model.getStatus())
            .storageSettingId(model.getExternalReference())
            .location(model.getLocation())
            .applicationContextProxy(applicationContextProxy)
            .amount(Integer.parseInt(model.getData().get(ProcessParamKeys.AMOUNT)))
            .reservedStorageId(uuidConverter.convertEntity(model.getData().get(ProcessParamKeys.RESERVED_STORAGE_ID)))
            .game(game)
            .build();
    }

    //TODO unit test
    public void save(Game game, StorageSetting storageSetting, UUID containerId, Integer amount) {
        UUID processId = idGenerator.randomUuid();

        ReservedStorage reservedStorage = reservedStorageFactory.save(
            game.getProgressDiff(),
            game.getData(),
            containerId,
            ContainerType.STORAGE,
            processId,
            storageSetting.getDataId(),
            amount
        );

        StorageSettingProcess process = StorageSettingProcess.builder()
            .processId(processId)
            .status(ProcessStatus.CREATED)
            .storageSettingId(storageSetting.getStorageSettingId())
            .location(storageSetting.getLocation())
            .applicationContextProxy(applicationContextProxy)
            .amount(amount)
            .reservedStorageId(reservedStorage.getReservedStorageId())
            .game(game)
            .build();

        game.getData()
            .getProcesses()
            .add(process);
        game.getProgressDiff()
            .save(process.toModel());
    }
}
