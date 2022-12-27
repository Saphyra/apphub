package com.github.saphyra.apphub.service.skyxplore.data.save_game;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.data.config.GameDataProperties;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemService;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.game.GameDao;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
@Builder
public class GameCleanupService {
    private final DateTimeUtil dateTimeUtil;
    private final GameDao gameDao;
    private final GameDataProperties properties;
    private final List<GameItemService> gameItemServices;

    @Transactional
    public void deleteMarkedGames() {
        LocalDateTime expirationDate = dateTimeUtil.getCurrentDateTime()
            .minusMinutes(properties.getGameDeletionExpirationMinutes());

        gameDao.getGamesMarkedForDeletion()
            .stream()
            .filter(gameModel -> isNull(gameModel.getMarkedForDeletionAt()) || gameModel.getMarkedForDeletionAt().isBefore(expirationDate))
            .forEach(gameModel -> gameItemServices.forEach(gameItemService -> gameItemService.deleteByGameId(gameModel.getGameId())));
    }
}
