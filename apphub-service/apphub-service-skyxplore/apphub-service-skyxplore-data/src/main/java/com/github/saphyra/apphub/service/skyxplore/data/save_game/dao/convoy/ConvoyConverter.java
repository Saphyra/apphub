package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.convoy;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConvoyModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class ConvoyConverter extends ConverterBase<ConvoyEntity, ConvoyModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected ConvoyEntity processDomainConversion(ConvoyModel domain) {
        return ConvoyEntity.builder()
            .convoyId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .resourceDeliveryRequestId(uuidConverter.convertDomain(domain.getResourceDeliveryRequestId()))
            .capacity(domain.getCapacity())
            .build();
    }

    @Override
    protected ConvoyModel processEntityConversion(ConvoyEntity entity) {
        ConvoyModel model = new ConvoyModel();
        model.setId(uuidConverter.convertEntity(entity.getConvoyId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.CONVOY);
        model.setResourceDeliveryRequestId(uuidConverter.convertEntity(entity.getResourceDeliveryRequestId()));
        model.setCapacity(entity.getCapacity());

        return model;
    }
}
