package com.github.saphyra.apphub.service.skyxplore.game.creation.service;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.GameFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.MessageSenderProxy;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
@Builder
public class GameCreationService {
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    private final MessageSenderProxy messageSenderProxy;
    private final GameFactory gameFactory;
    private final GameDao gameDao;
    private final BlockingQueue<SkyXploreGameCreationRequest> requests;

    private void create(SkyXploreGameCreationRequest request) {
        StopWatch stopWatch = StopWatch.createStarted();
        Game game = gameFactory.create(request);
        stopWatch.stop();
        log.info("Game created in {}ms", stopWatch.getTime(TimeUnit.MILLISECONDS));
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

    @PostConstruct
    public void createGames() {
        new Thread(() -> {
            while (true) {
                try {
                    SkyXploreGameCreationRequest request = requests.take();
                    executorService.submit(() -> {
                        try {
                            create(request);
                        } catch (Exception e) {
                            log.error("Exception during creating game", e);
                        }
                    });
                } catch (Exception e) {
                    log.error("Execution failed", e);
                }
            }
        }).start();
    }
}
