package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao;

import com.github.saphyra.apphub.api.skyxplore.game.client.SkyXploreGameApiClient;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.game.GameDao;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.player.PlayerDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameDeletionService implements DeleteByUserIdDao {
    private final GameDao gameDao;
    private final PlayerDao playerDao;
    private final DateTimeUtil dateTimeUtil;
    private final SkyXploreGameApiClient gameClient;
    private final LocaleProvider localeProvider;

    @Override
    @Transactional
    public void deleteByUserId(UUID userId) {
        log.info("Processing games for deletion of user {}", userId);
        List<UUID> deletedGameIds = gameDao.getByHost(userId)
            .stream()
            .map(GameItem::getGameId)
            .peek(gameId -> log.info("Deleting game by id {} of host {}", gameId, userId))
            .peek(this::markGameForDeletion)
            .collect(Collectors.toList());

        playerDao.getByUserId(userId)
            .stream()
            .filter(playerModel -> !deletedGameIds.contains(playerModel.getGameId()))
            .peek(playerModel -> log.info("Setting player {} to AI", playerModel.getId()))
            .peek(playerModel -> playerModel.setAi(true))
            .peek(playerModel -> playerModel.setUsername(playerModel.getUsername() + " (AI)"))
            .forEach(playerDao::save);
    }

    @Transactional
    public void deleteByGameId(UUID gameId, UUID userId) {
        GameModel gameModel = gameDao.findById(gameId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.GAME_NOT_FOUND, "Game not found with id " + gameId));

        if (!gameModel.getHost().equals(userId)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, userId + " must not delete game " + gameId);
        }

        markGameForDeletion(gameId);
    }

    private void markGameForDeletion(UUID gameId) {
        gameClient.deleteGame(gameId, localeProvider.getOrDefault());

        GameModel gameModel = gameDao.findByIdValidated(gameId);
        gameModel.setMarkedForDeletion(true);
        gameModel.setMarkedForDeletionAt(dateTimeUtil.getCurrentDateTime());

        gameDao.save(gameModel);
    }
}
