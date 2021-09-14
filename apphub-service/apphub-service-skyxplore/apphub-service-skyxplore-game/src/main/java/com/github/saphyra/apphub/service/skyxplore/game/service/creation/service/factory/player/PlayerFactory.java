package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.player;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.RandomNameProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlayerFactory {
    private final IdGenerator idGenerator;
    private final RandomNameProvider randomNameProvider;
    private final CharacterProxy characterProxy;

    public Map<UUID, Player> create(Map<UUID, UUID> members) {
        return members.entrySet()
            .stream()
            .map(entry -> create(entry.getKey(), false, entry.getValue(), characterProxy.getCharacterByUserId(entry.getKey()).getName()))
            .collect(Collectors.toMap(Player::getUserId, Function.identity()));
    }

    public Player createAi(List<String> usedPlayerNames) {
        UUID userId = idGenerator.randomUuid();
        String username = randomNameProvider.getRandomName(usedPlayerNames);
        log.debug("Username: {}", username);
        usedPlayerNames.add(username);

        return create(userId, true, null, username);
    }

    private Player create(UUID userId, boolean ai, UUID allianceId, String username) {
        log.debug("Creating player with name {}", username);
        Player player = Player.builder()
            .playerId(idGenerator.randomUuid())
            .userId(userId)
            .playerName(username)
            .allianceId(allianceId)
            .ai(ai)
            .connected(ai)
            .build();
        log.debug("Player generated: {}", player);
        return player;
    }
}
