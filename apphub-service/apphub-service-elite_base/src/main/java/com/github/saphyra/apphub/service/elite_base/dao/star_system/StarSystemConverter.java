package com.github.saphyra.apphub.service.elite_base.dao.star_system;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

//TODO unit test
@Component
@RequiredArgsConstructor
@Slf4j
class StarSystemConverter extends ConverterBase<StarSystemEntity, StarSystem> {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final UuidConverter uuidConverter;

    @Override
    protected StarSystemEntity processDomainConversion(StarSystem domain) {
        return StarSystemEntity.builder()
            .id(uuidConverter.convertDomain(domain.getId()))
            .lastUpdate(Optional.ofNullable(domain.getLastUpdate()).map(Object::toString).orElse(null))
            .starId(domain.getStarId())
            .starName(domain.getStarName())
            .position(objectMapperWrapper.writeValueAsString(domain.getPosition()))
            .starType(domain.getStarType())
            .build();
    }

    @Override
    protected StarSystem processEntityConversion(StarSystemEntity entity) {
        return StarSystem.builder()
            .id(uuidConverter.convertEntity(entity.getId()))
            .lastUpdate(LocalDateTime.parse(entity.getLastUpdate()))
            .starId(entity.getStarId())
            .starName(entity.getStarName())
            .position(objectMapperWrapper.readValue(entity.getPosition(), StarSystemPosition.class))
            .starType(entity.getStarType())
            .build();
    }
}
