package com.github.saphyra.apphub.service.skyxplore.game.ws.main.service;

import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.SystemMessage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.player.Player;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.game.ws.main.SkyXploreGameMainWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlayerConnectedService {
    private final GameDao gameDao;
    private final CharacterProxy characterProxy;

    public void playerConnected(UUID userId, SkyXploreGameMainWebSocketHandler webSocketHandler) {
        Game game = gameDao.findByUserIdValidated(userId);

        Player player = game.getPlayers()
            .get(userId);
        boolean isPlayerDisconnected = !isNull(player.getDisconnectedAt());

        player.setConnected(true);
        player.setDisconnectedAt(null);

        webSocketHandler.sendEvent(userId, WebSocketEventName.SKYXPLORE_GAME_PAUSED, game.isGamePaused());

        String characterName = characterProxy.getCharacterName(userId);

        game.getChat()
            .getRooms()
            .stream()
            .filter(chatRoom -> chatRoom.getMembers().contains(userId))
            .forEach(chatRoom -> webSocketHandler.sendEvent(
                game.filterConnectedPlayersFrom(chatRoom.getMembers()),
                WebSocketEventName.SKYXPLORE_GAME_USER_JOINED,
                new SystemMessage(chatRoom.getId(), characterName, userId))
            );

        game.setExpiresAt(null);

        if (isPlayerDisconnected) {
            webSocketHandler.sendEvent(game.getHost(), WebSocketEventName.SKYXPLORE_GAME_PLAYER_RECONNECTED, new OneParamResponse<>(player.getPlayerName()));
        }
    }
}
