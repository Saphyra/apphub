package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.storage_setting;

import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorageConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorageFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order.ProductionOrderProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class StorageSettingProcessHelper {
    private final ProductionOrderProcessFactory productionOrderProcessFactory;
    private final ReservedStorageFactory reservedStorageFactory;
    private final ReservedStorageConverter reservedStorageConverter;

    void orderResources(GameProgressDiff progressDiff, GameData gameData, UUID processId, StorageSetting storageSetting, int amount) {
        ReservedStorage reservedStorage = reservedStorageFactory.create(storageSetting.getLocation(), processId, storageSetting.getDataId(), amount);

        gameData.getReservedStorages()
            .add(reservedStorage);
        progressDiff.save(reservedStorageConverter.toModel(gameData.getGameId(), reservedStorage));

        productionOrderProcessFactory.create(gameData, processId, storageSetting.getLocation(), reservedStorage)
            .forEach(process -> {
                gameData.getProcesses()
                    .add(process);
                progressDiff.save(process.toModel());
            });
    }
}
