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
class ProductionOrderConverter extends ConverterBase<ProductionOrderEntity, ProductionOrderModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected ProductionOrderModel processEntityConversion(ProductionOrderEntity entity) {
        ProductionOrderModel model = new ProductionOrderModel();
        model.setId(uuidConverter.convertEntity(entity.getProductionOrderId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.PRODUCTION_ORDER);
        model.setLocation(uuidConverter.convertEntity(entity.getLocation()));
        model.setLocationType(entity.getLocationType());
        model.setAssignee(uuidConverter.convertEntity(entity.getAssignee()));
        model.setExternalReference(uuidConverter.convertEntity(entity.getExternalReference()));
        model.setDataId(entity.getDataId());
        model.setAmount(entity.getAmount());
        model.setRequiredWorkPoints(entity.getRequiredWorkPoints());
        model.setCurrentWorkPoints(entity.getCurrentWorkPoints());
        return model;
    }

    @Override
    protected ProductionOrderEntity processDomainConversion(ProductionOrderModel domain) {
        ProductionOrderEntity result = ProductionOrderEntity.builder()
            .productionOrderId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .location(uuidConverter.convertDomain(domain.getLocation()))
            .locationType(domain.getLocationType())
            .assignee(uuidConverter.convertDomain(domain.getAssignee()))
            .externalReference(uuidConverter.convertDomain(domain.getExternalReference()))
            .dataId(domain.getDataId())
            .amount(domain.getAmount())
            .requiredWorkPoints(domain.getRequiredWorkPoints())
            .currentWorkPoints(domain.getCurrentWorkPoints())
            .build();
        log.debug("Converted planet: {}", result);
        return result;
    }
}
