package com.github.saphyra.apphub.service.skyxplore.data.save_game.solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.UuidStringMap;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.skyxplore.data.common.CoordinateConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class SolarSystemConverter extends ConverterBase<SolarSystemEntity, SolarSystemModel> {
    private final UuidConverter uuidConverter;
    private final CoordinateConverter coordinateConverter;
    private final ObjectMapperWrapper objectMapperWrapper;

    @Override
    protected SolarSystemModel processEntityConversion(SolarSystemEntity entity) {
        SolarSystemModel model = new SolarSystemModel();
        model.setId(uuidConverter.convertEntity(entity.getSolarSystemId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setRadius(entity.getRadius());
        model.setDefaultName(entity.getDefaultName());
        model.setCustomNames(objectMapperWrapper.readValue(entity.getCustomNames(), UuidStringMap.class));
        model.setCoordinate(coordinateConverter.convertEntity(entity.getCoordinate()));
        return model;
    }

    @Override
    protected SolarSystemEntity processDomainConversion(SolarSystemModel domain) {
        return SolarSystemEntity.builder()
            .solarSystemId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .radius(domain.getRadius())
            .defaultName(domain.getDefaultName())
            .customNames(objectMapperWrapper.writeValueAsPrettyString(domain.getCustomNames()))
            .coordinate(coordinateConverter.convertDomain(domain.getCoordinate(), domain.getId()))
            .build();
    }
}
