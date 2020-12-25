package com.github.saphyra.apphub.service.skyxplore.game.controller;

import com.github.saphyra.apphub.api.skyxplore.game.server.SkyXploreGameController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SkyXploreGameControllerImpl implements SkyXploreGameController {
    private final GameDao gameDao;

    @Override
    //TODO unit test
    //TODO int test
    public boolean isUserInGame(AccessTokenHeader accessTokenHeader) {
        log.info("Checking if user {} is in game.", accessTokenHeader.getUserId());
        return gameDao.findByUserId(accessTokenHeader.getUserId()).isPresent();
    }
}
