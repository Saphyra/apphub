package com.github.saphyra.apphub.service.skyxplore.data.save_game.stored_resource;

import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class StoredResourceConverter extends ConverterBase<StoredResourceEntity, StoredResourceModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected StoredResourceModel processEntityConversion(StoredResourceEntity entity) {
        StoredResourceModel model = new StoredResourceModel();
        model.setId(uuidConverter.convertEntity(entity.getStoredResourceId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setLocation(uuidConverter.convertEntity(entity.getLocation()));
        model.setLocationType(entity.getLocationType());
        model.setDataId(entity.getDataId());
        model.setAmount(entity.getAmount());
        return model;
    }

    @Override
    protected StoredResourceEntity processDomainConversion(StoredResourceModel domain) {
        return StoredResourceEntity.builder()
            .storedResourceId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .location(uuidConverter.convertDomain(domain.getLocation()))
            .locationType(domain.getLocationType())
            .dataId(domain.getDataId())
            .amount(domain.getAmount())
            .build();
    }
}
