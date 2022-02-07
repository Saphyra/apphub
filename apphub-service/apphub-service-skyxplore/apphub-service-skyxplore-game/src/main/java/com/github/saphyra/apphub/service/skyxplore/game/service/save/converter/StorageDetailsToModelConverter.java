package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class StorageDetailsToModelConverter {
    private final AllocatedResourceToModelConverter allocatedResourceConverter;
    private final ReservedStorageToModelConverter reservedStorageConverter;
    private final StoredResourceToModelConverter storedResourceConverter;
    private final StorageSettingToModelConverter storageSettingConverter;

    List<GameItem> convertDeep(StorageDetails storageDetails, Game game) {
        List<GameItem> result = new ArrayList<>();
        result.addAll(allocatedResourceConverter.convert(storageDetails.getAllocatedResources(), game));
        result.addAll(reservedStorageConverter.convert(storageDetails.getReservedStorages(), game));
        result.addAll(storedResourceConverter.convert(storageDetails.getStoredResources(), game.getGameId()));
        result.addAll(storageSettingConverter.convert(storageDetails.getStorageSettings(), game));
        return result;
    }
}
