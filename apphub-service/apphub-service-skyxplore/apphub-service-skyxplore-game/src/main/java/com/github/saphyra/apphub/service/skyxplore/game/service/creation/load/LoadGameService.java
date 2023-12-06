package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreLoadGameRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeanFactory;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.GameLoader;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;

@Component
@Slf4j
public class LoadGameService {
    private final GameLoader gameLoader;
    private final ExecutorServiceBean executorServiceBean;
    private final GameDataProxy gameDataProxy;

    @Builder
    public LoadGameService(
        GameLoader gameLoader,
        GameDataProxy gameDataProxy,
        ExecutorServiceBeanFactory executorServiceBeanFactory
    ) {
        this.gameLoader = gameLoader;
        executorServiceBean = executorServiceBeanFactory.create(Executors.newSingleThreadExecutor());
        this.gameDataProxy = gameDataProxy;
    }

    public void loadGame(SkyXploreLoadGameRequest request) {
        GameModel game = gameDataProxy.getGameModel(request.getGameId());

        if (!game.getHost().equals(request.getHost())) {
            throw ExceptionFactory.notLoggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, request.getHost() + " must not load game " + request.getGameId());
        }

        executorServiceBean.execute(() -> gameLoader.loadGame(game, request.getPlayers()));
    }
}
