package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
import com.github.saphyra.apphub.lib.common_util.collection.UuidStringMap;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
class SolarSystemConverter extends ConverterBase<SolarSystemEntity, SolarSystemModel> {
    private final UuidConverter uuidConverter;
    private final ObjectMapper objectMapper;

    @Override
    protected SolarSystemModel processEntityConversion(SolarSystemEntity entity) {
        SolarSystemModel model = new SolarSystemModel();
        model.setId(uuidConverter.convertEntity(entity.getSolarSystemId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.SOLAR_SYSTEM);
        model.setRadius(entity.getRadius());
        model.setDefaultName(entity.getDefaultName());
        model.setCustomNames(objectMapper.readValue(entity.getCustomNames(), UuidStringMap.class));
        return model;
    }

    @Override
    protected SolarSystemEntity processDomainConversion(SolarSystemModel domain) {
        return SolarSystemEntity.builder()
            .solarSystemId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .radius(domain.getRadius())
            .defaultName(domain.getDefaultName())
            .customNames(objectMapper.writeValueAsString(domain.getCustomNames()))
            .build();
    }
}
