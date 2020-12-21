package com.github.saphyra.apphub.service.skyxplore.game.creation.service;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.common.MessageSenderProxy;
import com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.GameFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class GameCreationService {
    private final MessageSenderProxy messageSenderProxy;
    private final GameFactory gameFactory;
    private final GameDao gameDao;
    private final ObjectMapperWrapper objectMapperWrapper;

    public void create(SkyXploreGameCreationRequest request) {
        StopWatch stopWatch = StopWatch.createStarted();
        Game game = gameFactory.create(request);
        stopWatch.stop();
        log.info("Game created in {}ms", stopWatch.getTime(TimeUnit.MICROSECONDS));
        //log.info("Game created: {}", objectMapperWrapper.writeValueAsPrettyString(game));
        gameDao.save(game);

        WebSocketEvent event = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_LOBBY_GAME_LOADED)
            .build();

        WebSocketMessage message = WebSocketMessage.builder()
            .recipients(request.getMembers().keySet())
            .event(event)
            .build();

        messageSenderProxy.sendToLobby(message);
    }
}
