package com.github.saphyra.apphub.service.skyxplore.game.creation;

import java.util.concurrent.BlockingQueue;

import org.springframework.web.bind.annotation.RestController;

import com.github.saphyra.apphub.api.skyxplore.game.server.SkyXploreGameCreationController;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GameCreationControllerImpl implements SkyXploreGameCreationController {
    private final BlockingQueue<SkyXploreGameCreationRequest> requests;
    private final GameCreationRequestValidator validator;

    @Override
    public void createGame(SkyXploreGameCreationRequest request) throws InterruptedException {
        log.info("Creating game based on request {}", request);
        validator.validate(request);
        requests.put(request);
    }
}
