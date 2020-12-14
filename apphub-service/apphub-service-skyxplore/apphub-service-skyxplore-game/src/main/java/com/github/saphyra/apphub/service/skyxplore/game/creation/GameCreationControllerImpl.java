package com.github.saphyra.apphub.service.skyxplore.game.creation;

import com.github.saphyra.apphub.api.skyxplore.game.server.SkyXploreGameCreationController;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.lib.common_util.ExecutorServiceBean;
import com.github.saphyra.apphub.service.skyxplore.game.creation.service.GameCreationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GameCreationControllerImpl implements SkyXploreGameCreationController {
    private final GameCreationService gameCreationService;
    private final ExecutorServiceBean executorService;

    @Override
    //TODO unit test
    //TODO int test
    public void createGame(SkyXploreGameCreationRequest request) {
        log.info("Creating game based on request {}", request);
        executorService.execute(() -> gameCreationService.create(request));
    }
}
