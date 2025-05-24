package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.minor_faction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StarSystemMinorFactionMappingFactory {
    public StarSystemMinorFactionMapping create(UUID starSystemId, UUID minorFactionId) {
        return StarSystemMinorFactionMapping.builder()
            .starSystemId(starSystemId)
            .minorFactionId(minorFactionId)
            .build();
    }
}
