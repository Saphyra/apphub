package com.github.saphyra.apphub.service.skyxplore.data.save_game.game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.GameItemSaver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class GameSaverService implements GameItemSaver {
    private final GameDao gameDao;
    private final GameModelValidator gameModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        gameDao.deleteById(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.GAME;
    }

    @Override
    public void save(GameItem gameItem) {
        if (!(gameItem instanceof GameModel)) {
            throw new IllegalArgumentException("GameItem is not a " + getType() + ", it is " + gameItem.getType());
        }

        GameModel model = (GameModel) gameItem;
        gameModelValidator.validate(model);

        gameDao.save(model);
    }
}
