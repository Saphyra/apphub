package com.github.saphyra.apphub.service.feature.elite_base.dao.star_system;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StarSystemFactory {
    private final IdGenerator idGenerator;

    public StarSystem create(Long starId, String starName, Double[] starPosition, StarType starType) {
        return StarSystem.builder()
            .id(idGenerator.randomUuid())
            .starId(starId)
            .starName(starName)
            .position(StarSystemPosition.parse(starPosition))
            .starType(starType)
            .build();
    }
}
