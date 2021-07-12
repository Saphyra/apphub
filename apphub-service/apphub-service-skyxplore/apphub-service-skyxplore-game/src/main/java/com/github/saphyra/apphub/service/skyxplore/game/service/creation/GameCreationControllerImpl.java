package com.github.saphyra.apphub.service.skyxplore.game.service.creation;

import com.github.saphyra.apphub.api.skyxplore.game.server.SkyXploreGameCreationController;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreLoadGameRequest;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.LoadGameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.BlockingQueue;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GameCreationControllerImpl implements SkyXploreGameCreationController {
    private final BlockingQueue<SkyXploreGameCreationRequest> requests;
    private final GameCreationRequestValidator gameCreationRequestValidator;
    private final LoadGameService loadGameService;

    @Override
    public void createGame(SkyXploreGameCreationRequest request) throws InterruptedException {
        log.info("Creating game based on request {}", request);
        gameCreationRequestValidator.validate(request);
        requests.put(request);
    }

    @Override
    public void loadGame(SkyXploreLoadGameRequest request) {
        log.info("{} wants to load game {}", request.getHost(), request.getGameId());
        loadGameService.loadGame(request);
    }
}
