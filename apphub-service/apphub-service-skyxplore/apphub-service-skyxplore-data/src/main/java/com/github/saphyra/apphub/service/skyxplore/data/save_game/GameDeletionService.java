package com.github.saphyra.apphub.service.skyxplore.data.save_game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.game.GameDao;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.player.PlayerDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class GameDeletionService implements DeleteByUserIdDao {
    private final List<GameItemService> gameItemServices;
    private final GameDao gameDao;
    private final PlayerDao playerDao;

    @Override
    public void deleteByUserId(UUID userId) {
        log.info("Processing games for deletion of user {}", userId);
        playerDao.getByUserId(userId)
            .stream()
            .peek(playerModel -> playerModel.setAi(true))
            .forEach(playerDao::save);

        gameDao.getByHost(userId)
            .stream()
            .map(GameItem::getGameId)
            .forEach(gameId -> gameItemServices.forEach(gameItemService -> gameItemService.deleteByGameId(gameId)));
    }
}
