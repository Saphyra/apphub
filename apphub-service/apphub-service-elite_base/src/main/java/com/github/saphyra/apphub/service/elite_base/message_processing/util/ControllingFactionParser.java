package com.github.saphyra.apphub.service.elite_base.message_processing.util;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.ControllingFaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ControllingFactionParser {
    private final ObjectMapperWrapper objectMapperWrapper;

    public ControllingFaction parse(Object controllingFaction) {
        if (isNull(controllingFaction)) {
            return null;
        }

        if (controllingFaction instanceof String) {
            return ControllingFaction.builder()
                .factionName(controllingFaction.toString())
                .build();
        }

        return objectMapperWrapper.convertValue(controllingFaction, ControllingFaction.class);
    }
}
