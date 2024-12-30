package com.github.saphyra.apphub.service.elite_base.dao.star_system;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class StarSystemFactory {
    private final IdGenerator idGenerator;

    public StarSystem create(LocalDateTime timestamp, Long starId, String starName, Double[] starPosition) {
        return StarSystem.builder()
            .id(idGenerator.randomUuid())
            .lastUpdate(timestamp)
            .starId(starId)
            .starName(starName)
            .position(StarSystemPosition.parse(starPosition))
            .build();
    }
}
