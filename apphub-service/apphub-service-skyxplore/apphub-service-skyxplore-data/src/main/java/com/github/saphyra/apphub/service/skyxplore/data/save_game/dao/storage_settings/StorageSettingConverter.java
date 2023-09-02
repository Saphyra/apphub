package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.storage_settings;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class StorageSettingConverter extends ConverterBase<StorageSettingEntity, StorageSettingModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected StorageSettingModel processEntityConversion(StorageSettingEntity entity) {
        StorageSettingModel model = new StorageSettingModel();
        model.setId(uuidConverter.convertEntity(entity.getStorageSettingId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.STORAGE_SETTING);
        model.setLocation(uuidConverter.convertEntity(entity.getLocation()));
        model.setDataId(entity.getDataId());
        model.setTargetAmount(entity.getTargetAmount());
        model.setPriority(entity.getPriority());
        return model;
    }

    @Override
    protected StorageSettingEntity processDomainConversion(StorageSettingModel domain) {
        return StorageSettingEntity.builder()
            .storageSettingId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .location(uuidConverter.convertDomain(domain.getLocation()))
            .dataId(domain.getDataId())
            .targetAmount(domain.getTargetAmount())
            .priority(domain.getPriority())
            .build();
    }
}
