package com.github.saphyra.apphub.service.skyxplore.game.process.impl.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageSettingProcessFactory implements ProcessFactory {
    private final ApplicationContextProxy applicationContextProxy;
    private final IdGenerator idGenerator;

    public StorageSettingProcess create(Game game, Planet planet, StorageSetting storageSetting) {
        return StorageSettingProcess.builder()
            .processId(idGenerator.randomUuid())
            .status(ProcessStatus.CREATED)
            .storageSetting(storageSetting)
            .game(game)
            .planet(planet)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    @Override
    public ProcessType getType() {
        return ProcessType.STORAGE_SETTING;
    }

    @Override
    public StorageSettingProcess createFromModel(Game game, ProcessModel model) {
        Planet planet = game.getUniverse()
            .findPlanetByIdValidated(model.getLocation());

        return StorageSettingProcess.builder()
            .processId(model.getId())
            .status(model.getStatus())
            .storageSetting(planet.getStorageDetails().getStorageSettings().findByStorageSettingIdValidated(model.getExternalReference()))
            .game(game)
            .planet(planet)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }
}
