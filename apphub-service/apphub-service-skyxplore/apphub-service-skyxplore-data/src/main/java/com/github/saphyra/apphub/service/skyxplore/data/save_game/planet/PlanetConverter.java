package com.github.saphyra.apphub.service.skyxplore.data.save_game.planet;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlanetModel;
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
class PlanetConverter extends ConverterBase<PlanetEntity, PlanetModel> {
    private final UuidConverter uuidConverter;
    private final ObjectMapperWrapper objectMapperWrapper;
    private final CoordinateConverter coordinateConverter;

    @Override
    protected PlanetModel processEntityConversion(PlanetEntity entity) {
        PlanetModel model = new PlanetModel();
        model.setId(uuidConverter.convertEntity(entity.getPlanetId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.PLANET);
        model.setSolarSystemId(uuidConverter.convertEntity(entity.getSolarSystemId()));
        model.setDefaultName(entity.getDefaultName());
        model.setCustomNames(objectMapperWrapper.readValue(entity.getCustomNames(), UuidStringMap.class));
        model.setCoordinate(coordinateConverter.convertEntity(entity.getCoordinate()));
        model.setSize(entity.getSize());
        model.setOwner(uuidConverter.convertEntity(entity.getOwner()));
        return model;
    }

    @Override
    protected PlanetEntity processDomainConversion(PlanetModel domain) {
        PlanetEntity result = PlanetEntity.builder()
            .planetId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .solarSystemId(uuidConverter.convertDomain(domain.getSolarSystemId()))
            .defaultName(domain.getDefaultName())
            .customNames(objectMapperWrapper.writeValueAsString(domain.getCustomNames()))
            .coordinate(coordinateConverter.convertDomain(domain.getCoordinate(), domain.getId()))
            .size(domain.getSize())
            .owner(uuidConverter.convertDomain(domain.getOwner()))
            .build();
        log.info("Converted planet: {}", result);
        return result;
    }
}
