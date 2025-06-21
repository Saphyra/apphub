package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.production_request;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionRequestModel;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class ProductionRequestConverter extends ConverterBase<ProductionRequestEntity, ProductionRequestModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected ProductionRequestEntity processDomainConversion(ProductionRequestModel domain) {
        return ProductionRequestEntity.builder()
            .productionRequestId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .reservedStorageId(uuidConverter.convertDomain(domain.getReservedStorageId()))
            .requestedAmount(domain.getRequestedAmount())
            .dispatchedAmount(domain.getDispatchedAmount())
            .build();
    }

    @Override
    protected ProductionRequestModel processEntityConversion(ProductionRequestEntity entity) {
        ProductionRequestModel model = new ProductionRequestModel();
        model.setId(uuidConverter.convertEntity(entity.getProductionRequestId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.PRODUCTION_REQUEST);
        model.setReservedStorageId(uuidConverter.convertEntity(entity.getReservedStorageId()));
        model.setRequestedAmount(entity.getRequestedAmount());
        model.setDispatchedAmount(entity.getDispatchedAmount());

        return model;
    }
}
