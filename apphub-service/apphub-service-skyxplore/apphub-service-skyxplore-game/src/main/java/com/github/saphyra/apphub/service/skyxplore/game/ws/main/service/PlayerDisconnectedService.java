package com.github.saphyra.apphub.service.skyxplore.game.ws.main.service;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.config.CommonSkyXploreConfiguration;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.SystemMessage;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.game.ws.main.SkyXploreGameMainWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlayerDisconnectedService {
    private final GameDao gameDao;
    private final CharacterProxy characterProxy;
    private final DateTimeUtil dateTimeUtil;
    private final CommonSkyXploreConfiguration configuration;

    public void playerDisconnected(UUID userId, SkyXploreGameMainWebSocketHandler webSocketHandler){
        gameDao.findByUserId(userId).ifPresent(game -> {
            game.getPlayers()
                .get(userId)
                .setConnected(false);

            if (game.getConnectedPlayers().isEmpty()) {
                game.setExpiresAt(dateTimeUtil.getCurrentDateTime().plusSeconds(configuration.getAbandonedGameExpirationSeconds()));
                return;
            }

            game.setGamePaused(true);
            webSocketHandler.sendEvent(game.getConnectedPlayers(), WebSocketEventName.SKYXPLORE_GAME_PAUSED, true);

            String characterName = characterProxy.getCharacterName(userId);

            game.getChat()
                .getRooms()
                .stream()
                .filter(chatRoom -> chatRoom.getMembers().contains(userId))
                .forEach(chatRoom -> webSocketHandler.sendEvent(
                    game.filterConnectedPlayersFrom(chatRoom.getMembers()),
                    WebSocketEventName.SKYXPLORE_GAME_USER_LEFT,
                    new SystemMessage(chatRoom.getId(), characterName, userId))
                );
        });
    }
}
