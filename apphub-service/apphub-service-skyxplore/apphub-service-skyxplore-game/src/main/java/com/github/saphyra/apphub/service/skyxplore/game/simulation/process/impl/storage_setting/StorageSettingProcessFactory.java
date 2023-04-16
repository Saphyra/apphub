package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class StorageSettingProcessFactory implements ProcessFactory {
    private final ApplicationContextProxy applicationContextProxy;
    private final IdGenerator idGenerator;

    public StorageSettingProcess create(GameData gameData, StorageSetting storageSetting, int amount) {
        return StorageSettingProcess.builder()
            .processId(idGenerator.randomUuid())
            .status(ProcessStatus.CREATED)
            .storageSettingId(storageSetting.getStorageSettingId())
            .gameData(gameData)
            .location(storageSetting.getLocation())
            .applicationContextProxy(applicationContextProxy)
            .amount(amount)
            .build();
    }

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
            .gameData(game.getData())
            .location(model.getLocation())
            .applicationContextProxy(applicationContextProxy)
            .amount(Integer.parseInt(model.getData().get(ProcessParamKeys.AMOUNT)))
            .build();
    }
}
