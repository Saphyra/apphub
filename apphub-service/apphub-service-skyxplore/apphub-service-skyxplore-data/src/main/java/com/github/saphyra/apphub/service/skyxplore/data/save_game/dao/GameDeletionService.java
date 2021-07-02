package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.game.GameDao;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.player.PlayerDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameDeletionService implements DeleteByUserIdDao {
    private final List<GameItemService> gameItemServices;
    private final GameDao gameDao;
    private final PlayerDao playerDao;

    @Override
    public void deleteByUserId(UUID userId) {
        log.info("Processing games for deletion of user {}", userId);
        playerDao.getByUserId(userId)
            .stream()
            .peek(playerModel -> playerModel.setAi(true))
            .peek(playerModel -> playerModel.setUsername(playerModel.getUsername() + " (AI)"))
            .forEach(playerDao::save);

        gameDao.getByHost(userId)
            .stream()
            .map(GameItem::getGameId)
            .peek(gameId -> log.info("Deleting game by id {} of host {}", gameId, userId))
            .forEach(this::deleteByGamaId);
    }

    public void deleteByGameId(UUID gameId, UUID userId) {
        GameModel gameModel = gameDao.findById(gameId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, "Game not found with id " + gameId));

        if (!gameModel.getHost().equals(userId)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, userId + " must not delete game " + gameId);
        }

        deleteByGamaId(gameId);
    }

    private void deleteByGamaId(UUID gameId) {
        gameItemServices.forEach(gameItemService -> gameItemService.deleteByGameId(gameId));
    }
}
