package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.priority;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PriorityModel;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class PriorityConverter extends ConverterBase<PriorityEntity, PriorityModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected PriorityModel processEntityConversion(PriorityEntity entity) {
        PriorityModel model = new PriorityModel();
        model.setId(uuidConverter.convertEntity(entity.getPriorityId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.PRIORITY);
        model.setLocation(uuidConverter.convertEntity(entity.getLocation()));
        model.setPriorityType(entity.getPriorityType());
        model.setValue(entity.getValue());
        return model;
    }

    @Override
    protected PriorityEntity processDomainConversion(PriorityModel domain) {
        return PriorityEntity.builder()
            .priorityId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .location(uuidConverter.convertDomain(domain.getLocation()))
            .priorityType(domain.getPriorityType())
            .value(domain.getValue())
            .build();
    }
}
