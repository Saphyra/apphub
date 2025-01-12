package com.github.saphyra.apphub.service.elite_base.message_processing.util;

import com.github.saphyra.apphub.service.elite_base.message_processing.dao.minor_faction.MinorFaction;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.MinorFactionSaver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class MinorFactionIdResolver {
    private final MinorFactionSaver minorFactionSaver;

    public UUID getMinorFactionId(LocalDateTime timestamp, String factionName, List<MinorFaction> minorFactions) {
        minorFactions = isNull(minorFactions) ? Collections.emptyList() : minorFactions;

        return minorFactions.stream()
            .filter(minorFaction -> minorFaction.getFactionName().equalsIgnoreCase(factionName))
            .findAny()
            .map(MinorFaction::getId)
            .orElseGet(() -> minorFactionSaver.save(timestamp, factionName, null).getId());
    }
}
