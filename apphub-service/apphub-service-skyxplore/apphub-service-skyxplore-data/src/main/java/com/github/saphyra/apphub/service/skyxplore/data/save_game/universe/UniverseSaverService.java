package com.github.saphyra.apphub.service.skyxplore.data.save_game.universe;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.UniverseModel;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.GameItemSaver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class UniverseSaverService implements GameItemSaver {
    private final UniverseDao universeDao;
    private final UniverseModelValidator universeModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        universeDao.deleteById(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.UNIVERSE;
    }

    @Override
    public void save(GameItem gameItem) {
        if (!(gameItem instanceof UniverseModel)) {
            throw new IllegalArgumentException("GameItem is not a " + getType() + ", it is " + gameItem.getType());
        }

        UniverseModel model = (UniverseModel) gameItem;
        universeModelValidator.validate(model);

        universeDao.save(model);
    }
}
