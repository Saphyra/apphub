package com.github.saphyra.apphub.service.skyxplore.game.ws.service;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.SystemMessage;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.game.ws.SkyXploreGameWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class PlayerConnectedService {
    private final GameDao gameDao;
    private final CharacterProxy characterProxy;

    public void playerConnected(UUID userId, SkyXploreGameWebSocketHandler webSocketHandler){
        Game game = gameDao.findByUserIdValidated(userId);

        game.getPlayers()
            .get(userId)
            .setConnected(true);

        webSocketHandler.sendEvent(userId, WebSocketEventName.SKYXPLORE_GAME_PAUSED, game.isGamePaused());

        String userName = characterProxy.getCharacterByUserId(userId).getName();

        game.getChat()
            .getRooms()
            .stream()
            .filter(chatRoom -> chatRoom.getMembers().contains(userId))
            .forEach(chatRoom -> webSocketHandler.sendEvent(
                game.filterConnectedPlayersFrom(chatRoom.getMembers()),
                WebSocketEventName.SKYXPLORE_GAME_USER_JOINED,
                new SystemMessage(chatRoom.getId(), userName, userId))
            );

        game.setExpiresAt(null);
    }
}
