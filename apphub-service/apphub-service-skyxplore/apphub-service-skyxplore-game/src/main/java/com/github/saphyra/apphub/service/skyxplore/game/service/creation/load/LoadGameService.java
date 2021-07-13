package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreLoadGameRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.GameLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class LoadGameService {
    private final GameItemLoader gameItemLoader;
    private final ExecutorServiceBean executorServiceBean;
    private final GameLoader gameLoader;
    private final LoadGameRequestValidator loadGameRequestValidator;

    public LoadGameService(
        GameItemLoader gameItemLoader,
        SleepService sleepService,
        GameLoader gameLoader,
        LoadGameRequestValidator loadGameRequestValidator) {
        this.gameItemLoader = gameItemLoader;
        executorServiceBean = new ExecutorServiceBean(Executors.newSingleThreadExecutor(), sleepService);
        this.gameLoader = gameLoader;
        this.loadGameRequestValidator = loadGameRequestValidator;
    }

    public void loadGame(SkyXploreLoadGameRequest request) {
        loadGameRequestValidator.validate(request);

        Optional<GameModel> o = gameItemLoader.loadItem(request.getGameId(), GameItemType.GAME);
        GameModel game = o.orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.GAME_NOT_FOUND, "Game not found with id " + request.getGameId()));
        log.info("GameModel: {}", game);

        if (!game.getHost().equals(request.getHost())) {
            throw ExceptionFactory.notLoggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, request.getHost() + " must not load game " + request.getGameId());
        }

        executorServiceBean.execute(() -> gameLoader.loadGame(game, request.getMembers()));
    }
}
