package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageSettingDeletionService {
    private final GameDao gameDao;
    private final GameDataProxy gameDataProxy;

    public void deleteStorageSetting(UUID userId, UUID planetId, UUID storageSettingId) {
        StorageDetails storageDetails = gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findByOwnerAndPlanetIdValidated(userId, planetId)
            .getStorageDetails();

        storageDetails.getStorageSettings()
            .deleteByStorageSettingId(storageSettingId);

        gameDataProxy.deleteItem(storageSettingId, GameItemType.STORAGE_SETTING);
    }
}
