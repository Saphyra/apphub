package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.player;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlayerService implements GameItemService {
    private final PlayerDao playerDao;
    private final PlayerModelValidator playerModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        log.info("Deleting {}s by gameId {}", getClass().getSimpleName(), gameId);
        playerDao.deleteByGameId(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.PLAYER;
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<PlayerModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof PlayerModel)
            .map(gameItem -> (PlayerModel) gameItem)
            .peek(playerModelValidator::validate)
            .collect(Collectors.toList());

        playerDao.saveAll(models);
    }

    @Override
    public Optional<PlayerModel> findById(UUID id) {
        return playerDao.findById(id);
    }

    @Override
    public List<PlayerModel> getByParent(UUID parent) {
        return playerDao.getByGameId(parent);
    }

    @Override
    public void deleteById(UUID id) {
        playerDao.deleteById(id);
    }
}