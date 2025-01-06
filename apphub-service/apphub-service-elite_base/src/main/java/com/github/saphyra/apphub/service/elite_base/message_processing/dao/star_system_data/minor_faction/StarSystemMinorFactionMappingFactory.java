package com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.minor_faction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class StarSystemMinorFactionMappingFactory {
    public StarSystemMinorFactionMapping create(UUID starSystemId, UUID minorFactionId) {
        return StarSystemMinorFactionMapping.builder()
            .starSystemId(starSystemId)
            .minorFactionId(minorFactionId)
            .build();
    }
}
