package com.github.saphyra.apphub.service.feature.skyxplore.game.service.creation.generation;

import com.github.saphyra.apphub.api.feature.skyxplore.lobby.client.SkyXploreLobbyApiClient;
import com.github.saphyra.apphub.api.feature.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeanFactory;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.feature.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.feature.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.feature.skyxplore.game.service.creation.generation.factory.GameFactory;
import com.github.saphyra.apphub.service.feature.skyxplore.game.simulation.tick.TickSchedulerLauncher;
import jakarta.annotation.PostConstruct;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class GameCreationService {
    private final ExecutorServiceBean executorServiceBean;
    private final SkyXploreLobbyApiClient lobbyClient;
    private final GameFactory gameFactory;
    private final GameDao gameDao;
    private final BlockingQueue<BiWrapper<SkyXploreGameCreationRequest, UUID>> requests;
    private final GameSaverService gameSaverService;
    private final ErrorReporterService errorReporterService;
    private final TickSchedulerLauncher tickSchedulerLauncher;

    @Builder
    GameCreationService(
        SkyXploreLobbyApiClient lobbyClient,
        GameFactory gameFactory,
        GameDao gameDao,
        BlockingQueue<BiWrapper<SkyXploreGameCreationRequest, UUID>> requests,
        GameSaverService gameSaverService,
        ExecutorServiceBeanFactory executorServiceBeanFactory,
        ErrorReporterService errorReporterService,
        TickSchedulerLauncher tickSchedulerLauncher
    ) {
        this.lobbyClient = lobbyClient;
        this.gameFactory = gameFactory;
        this.gameDao = gameDao;
        this.requests = requests;
        this.gameSaverService = gameSaverService;
        executorServiceBean = executorServiceBeanFactory.create(Executors.newFixedThreadPool(3));
        this.errorReporterService = errorReporterService;
        this.tickSchedulerLauncher = tickSchedulerLauncher;
    }

    private void create(SkyXploreGameCreationRequest request, UUID gameId) {
        StopWatch stopWatch = StopWatch.createStarted();
        Game game = gameFactory.create(request, gameId);
        stopWatch.stop();
        log.info("Game {} created in {}ms for host {}", game.getGameId(), stopWatch.getTime(TimeUnit.MILLISECONDS), game.getHost());
        gameDao.save(game);
        gameSaverService.save(game);
        tickSchedulerLauncher.launch(game);

        lobbyClient.gameLoaded(gameId);
    }

    @PostConstruct
    public void createGames() {
        new Thread(() -> {
            log.info("GameCreation Thread started.");
            while (true) {
                try {
                    BiWrapper<SkyXploreGameCreationRequest, UUID> biWrapper = requests.take();
                    executorServiceBean.execute(() -> create(biWrapper.getEntity1(), biWrapper.getEntity2()));
                } catch (Exception e) {
                    errorReporterService.report("GameCreation failed.", e);
                }
                //Sleeping is not necessary, because requests.take() blocks the thread until new item is put into the queue
            }
        }).start();
    }
}
