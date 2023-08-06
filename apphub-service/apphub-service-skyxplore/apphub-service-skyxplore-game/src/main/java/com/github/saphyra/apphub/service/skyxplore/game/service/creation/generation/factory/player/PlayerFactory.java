package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.player;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.AiPlayer;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.player.Player;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.CharacterProxy;
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
public class PlayerFactory {
    private final IdGenerator idGenerator;
    private final CharacterProxy characterProxy;

    public Map<UUID, Player> create(Map<UUID, UUID> members) {
        return members.entrySet()
            .stream()
            .map(entry -> create(entry.getKey(), false, entry.getValue(), characterProxy.getCharacterByUserId(entry.getKey()).getName()))
            .collect(Collectors.toMap(Player::getUserId, Function.identity()));
    }

    public Player createAi(AiPlayer aiPlayer) {
        return create(aiPlayer.getUserId(), true, aiPlayer.getAllianceId(), aiPlayer.getName());
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
