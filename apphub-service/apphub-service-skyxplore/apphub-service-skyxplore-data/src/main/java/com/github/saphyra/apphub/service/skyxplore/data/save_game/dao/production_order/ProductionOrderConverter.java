package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionOrderModel;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ProductionOrderConverter extends ConverterBase<ProductionOrderEntity, ProductionOrderModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected ProductionOrderEntity processDomainConversion(ProductionOrderModel domain) {
        return ProductionOrderEntity.builder()
            .productionOrderId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .productionRequestId(uuidConverter.convertDomain(domain.getProductionRequestId()))
            .constructionAreaId(uuidConverter.convertDomain(domain.getConstructionAreaId()))
            .resourceDataId(domain.getResourceDataId())
            .requestedAmount(domain.getRequestedAmount())
            .startedAmount(domain.getStartedAmount())
            .build();
    }

    @Override
    protected ProductionOrderModel processEntityConversion(ProductionOrderEntity entity) {
        ProductionOrderModel model = new ProductionOrderModel();
        model.setId(uuidConverter.convertEntity(entity.getProductionOrderId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.PRODUCTION_ORDER);
        model.setProductionRequestId(uuidConverter.convertEntity(entity.getProductionRequestId()));
        model.setConstructionAreaId(uuidConverter.convertEntity(entity.getConstructionAreaId()));
        model.setResourceDataId(entity.getResourceDataId());
        model.setRequestedAmount(entity.getRequestedAmount());
        model.setStartedAmount(entity.getStartedAmount());

        return model;
    }
}
