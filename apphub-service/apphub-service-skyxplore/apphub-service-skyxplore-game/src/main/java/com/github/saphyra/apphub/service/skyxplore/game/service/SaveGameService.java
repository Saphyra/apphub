package com.github.saphyra.apphub.service.skyxplore.game.service;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SaveGameService {
    private final GameDao gameDao;
    private final GameDataProxy gameDataProxy;
    private final GameConverter gameConverter;
    private final DateTimeUtil dateTimeUtil;

    public void saveGame(UUID userId) {
        Game game = gameDao.findByUserIdValidated(userId);

        if (!game.getHost().equals(userId)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, userId + " is not a host of game " + game.getGameId());
        }

        game.getEventLoop()
            .processWithWait(() -> {
                game.setLastPlayed(dateTimeUtil.getCurrentDateTime());
                GameModel gameModel = gameConverter.convert(game);

                GameProgressDiff progressDiff = game.getProgressDiff();
                progressDiff.save(gameModel);
                progressDiff.process(gameDataProxy);
            })
            .getOrThrow();
    }
}
