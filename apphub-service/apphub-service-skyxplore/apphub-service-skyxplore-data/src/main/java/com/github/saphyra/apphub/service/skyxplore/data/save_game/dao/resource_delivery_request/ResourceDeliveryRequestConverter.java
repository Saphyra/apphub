package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.resource_delivery_request;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ResourceDeliveryRequestModel;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class ResourceDeliveryRequestConverter extends ConverterBase<ResourceDeliveryRequestEntity, ResourceDeliveryRequestModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected ResourceDeliveryRequestEntity processDomainConversion(ResourceDeliveryRequestModel domain) {
        return ResourceDeliveryRequestEntity.builder()
            .resourceDeliveryRequestId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .reservedStorageId(uuidConverter.convertDomain(domain.getReservedStorageId()))
            .build();
    }

    @Override
    protected ResourceDeliveryRequestModel processEntityConversion(ResourceDeliveryRequestEntity entity) {
        ResourceDeliveryRequestModel model = new ResourceDeliveryRequestModel();
        model.setId(uuidConverter.convertEntity(entity.getResourceDeliveryRequestId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.RESOURCE_DELIVERY_REQUEST);
        model.setReservedStorageId(uuidConverter.convertEntity(entity.getReservedStorageId()));

        return model;
    }
}
