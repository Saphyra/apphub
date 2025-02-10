package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system;

import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
class StarSystemConverter extends ConverterBase<StarSystemEntity, StarSystem> {
    private final UuidConverter uuidConverter;
    private final DateTimeConverter dateTimeConverter;

    @Override
    protected StarSystemEntity processDomainConversion(StarSystem domain) {
        return StarSystemEntity.builder()
            .id(uuidConverter.convertDomain(domain.getId()))
            .lastUpdate(Optional.ofNullable(domain.getLastUpdate()).map(dateTimeConverter::convertDomain).orElse(null))
            .starId(domain.getStarId())
            .starName(domain.getStarName())
            .xPos(Optional.ofNullable(domain.getPosition()).map(StarSystemPosition::getX).orElse(null))
            .yPos(Optional.ofNullable(domain.getPosition()).map(StarSystemPosition::getY).orElse(null))
            .zPos(Optional.ofNullable(domain.getPosition()).map(StarSystemPosition::getZ).orElse(null))
            .starType(domain.getStarType())
            .build();
    }

    @Override
    protected StarSystem processEntityConversion(StarSystemEntity entity) {
        return StarSystem.builder()
            .id(uuidConverter.convertEntity(entity.getId()))
            .lastUpdate(dateTimeConverter.convertToLocalDateTime(entity.getLastUpdate()))
            .starId(entity.getStarId())
            .starName(entity.getStarName())
            .position(StarSystemPosition.builder()
                .x(entity.getXPos())
                .y(entity.getYPos())
                .z(entity.getZPos())
                .build())
            .starType(entity.getStarType())
            .build();
    }
}
