package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeanFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.GameFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.GameSaverService;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class GameCreationService {
    private final ExecutorServiceBean executorServiceBean;
    private final MessageSenderProxy messageSenderProxy;
    private final GameFactory gameFactory;
    private final GameDao gameDao;
    private final BlockingQueue<SkyXploreGameCreationRequest> requests;
    private final GameSaverService gameSaverService;

    @Builder
    public GameCreationService(
        MessageSenderProxy messageSenderProxy,
        GameFactory gameFactory,
        GameDao gameDao,
        BlockingQueue<SkyXploreGameCreationRequest> requests,
        GameSaverService gameSaverService,
        ExecutorServiceBeanFactory executorServiceBeanFactory
    ) {
        this.messageSenderProxy = messageSenderProxy;
        this.gameFactory = gameFactory;
        this.gameDao = gameDao;
        this.requests = requests;
        this.gameSaverService = gameSaverService;
        executorServiceBean = executorServiceBeanFactory.create(Executors.newFixedThreadPool(3));
    }

    private void create(SkyXploreGameCreationRequest request) {
        StopWatch stopWatch = StopWatch.createStarted();
        Game game = gameFactory.create(request);
        stopWatch.stop();
        log.info("Game {} created in {}ms for host {}", game.getGameId(), stopWatch.getTime(TimeUnit.MILLISECONDS), game.getHost());
        gameDao.save(game);
        gameSaverService.save(game);

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
                    executorServiceBean.execute(() -> create(request));
                } catch (Exception e) {
                    log.error("Execution failed", e);
                }
                //Sleeping is not necessary, because requests.take() blocks the thread until new item is put into the queue
            }
        }).start();
    }
}
