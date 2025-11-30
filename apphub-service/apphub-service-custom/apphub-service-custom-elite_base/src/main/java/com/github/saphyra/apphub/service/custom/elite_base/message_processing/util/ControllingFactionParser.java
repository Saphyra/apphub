package com.github.saphyra.apphub.service.custom.elite_base.message_processing.util;

import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.ControllingFaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class ControllingFactionParser {
    private final ObjectMapper objectMapper;

    public ControllingFaction parse(Object controllingFaction) {
        if (isNull(controllingFaction)) {
            return null;
        }

        if (controllingFaction instanceof String) {
            return ControllingFaction.builder()
                .factionName(controllingFaction.toString())
                .build();
        }

        return objectMapper.convertValue(controllingFaction, ControllingFaction.class);
    }
}
