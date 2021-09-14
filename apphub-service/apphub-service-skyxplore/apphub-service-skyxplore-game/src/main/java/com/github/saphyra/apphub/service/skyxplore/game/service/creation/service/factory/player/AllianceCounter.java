package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.player;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class AllianceCounter {
    int getAllianceCount(Map<UUID, UUID> members) {
        Set<UUID> alliances = new HashSet<>();
        int memberWithoutAlliance = 0;
        for (Map.Entry<UUID, UUID> entry : members.entrySet()) {
            if (isNull(entry.getValue())) {
                memberWithoutAlliance++;
            } else {
                alliances.add(entry.getKey());
            }
        }

        return alliances.size() + memberWithoutAlliance;
    }
}
