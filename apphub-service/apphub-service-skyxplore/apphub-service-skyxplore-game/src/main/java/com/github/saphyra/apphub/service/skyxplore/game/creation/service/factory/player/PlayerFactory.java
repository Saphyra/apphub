package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.player;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.creation.service.RandomNameProvider;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.CharacterProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class PlayerFactory {
    private final IdGenerator idGenerator;
    private final RandomNameProvider randomNameProvider;
    private final CharacterProxy characterProxy;

    Player create(UUID userId, boolean ai, List<String> usedPlayerNames) {
        String username = ai ? randomNameProvider.getRandomName(usedPlayerNames) : characterProxy.getCharacterByUserId(userId).getName();
        log.debug("Username: {}", username);
        usedPlayerNames.add(username);

        log.debug("Creating player with name {}", username);
        Player player = Player.builder()
            .playerId(idGenerator.randomUuid())
            .userId(userId)
            .username(username)
            .ai(ai)
            .connected(ai)
            .build();
        log.debug("Player generated: {}", player);
        return player;
    }
}
