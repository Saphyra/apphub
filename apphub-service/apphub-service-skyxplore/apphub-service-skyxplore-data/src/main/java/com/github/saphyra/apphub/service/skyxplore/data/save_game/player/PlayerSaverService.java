package com.github.saphyra.apphub.service.skyxplore.data.save_game.player;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.GameItemSaver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class PlayerSaverService implements GameItemSaver {
    private final PlayerDao playerDao;
    private final PlayerModelValidator playerModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
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
}
