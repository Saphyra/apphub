package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory;

import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class AllianceFactory {
    /**
     * @param alliances <AllianceId, AllianceName>
     * @param members   <UserId, AllianceId>
     * @param players   <UserId, Player>
     */
    public Map<UUID, Alliance> create(Map<UUID, String> alliances, Map<UUID, UUID> members, Map<UUID, Player> players) {
        log.info("Generating alliances...");
        return alliances.entrySet()
            .stream()
            .map(alliance -> Alliance.builder()
                .allianceId(alliance.getKey())
                .allianceName(alliance.getValue())
                .members(getMembers(alliance.getKey(), members, players))
                .build()
            )
            .collect(Collectors.toMap(Alliance::getAllianceId, Function.identity()));
    }

    private Map<UUID, Player> getMembers(UUID alliance, Map<UUID, UUID> members, Map<UUID, Player> players) {
        return members.entrySet()
            .stream()
            .filter(entry -> alliance.equals(entry.getValue()))
            .map(Map.Entry::getKey)
            .map(players::get)
            .collect(Collectors.toMap(Player::getUserId, Function.identity()));
    }
}
