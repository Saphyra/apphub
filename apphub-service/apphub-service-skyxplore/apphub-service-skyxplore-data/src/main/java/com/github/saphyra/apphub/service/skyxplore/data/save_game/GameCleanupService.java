package com.github.saphyra.apphub.service.skyxplore.data.save_game;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.data.config.GameDataProperties;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemService;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.game.GameDao;
import com.github.saphyra.apphub.service.skyxplore.data.setting.dao.SettingDao;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

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
    private final SettingDao settingDao;

    @Transactional
    public void deleteMarkedGames() {
        LocalDateTime expirationDate = dateTimeUtil.getCurrentDateTime()
            .minusMinutes(properties.getGameDeletionExpirationMinutes());

        gameDao.getGamesMarkedForDeletion()
            .stream()
            .filter(gameModel -> isNull(gameModel.getMarkedForDeletionAt()) || gameModel.getMarkedForDeletionAt().isBefore(expirationDate))
            .peek(gameModel -> settingDao.deleteByGameId(gameModel.getGameId()))
            .forEach(gameModel -> gameItemServices.forEach(gameItemService -> gameItemService.deleteByGameId(gameModel.getGameId())));
    }
}
