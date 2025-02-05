package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class StarSystemFactory {
    private final IdGenerator idGenerator;

    public StarSystem create(LocalDateTime timestamp, Long starId, String starName, Double[] starPosition, StarType starType) {
        return StarSystem.builder()
            .id(idGenerator.randomUuid())
            .lastUpdate(timestamp)
            .starId(starId)
            .starName(starName)
            .position(StarSystemPosition.parse(starPosition))
            .starType(starType)
            .build();
    }
}
