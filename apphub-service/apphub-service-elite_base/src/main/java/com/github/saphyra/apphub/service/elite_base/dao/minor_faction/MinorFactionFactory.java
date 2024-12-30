package com.github.saphyra.apphub.service.elite_base.dao.minor_faction;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class MinorFactionFactory {
    private final IdGenerator idGenerator;

    public MinorFaction create(LocalDateTime timestamp, String factionName, FactionState economicState) {
        return MinorFaction.builder()
            .id(idGenerator.randomUuid())
            .lastUpdate(timestamp)
            .factionName(factionName)
            .state(economicState)
            .build();
    }
}
