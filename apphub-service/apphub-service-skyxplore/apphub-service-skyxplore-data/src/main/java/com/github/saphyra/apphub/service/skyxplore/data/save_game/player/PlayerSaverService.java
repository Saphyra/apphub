package com.github.saphyra.apphub.service.skyxplore.data.save_game.player;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.GameItemSaver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
    public void save(GameItem gameItem) {
        if (!(gameItem instanceof PlayerModel)) {
            throw new IllegalArgumentException("GameItem is not a " + getType() + ", it is " + gameItem.getType());
        }

        PlayerModel model = (PlayerModel) gameItem;
        playerModelValidator.validate(model);

        playerDao.save(model);
    }
}
