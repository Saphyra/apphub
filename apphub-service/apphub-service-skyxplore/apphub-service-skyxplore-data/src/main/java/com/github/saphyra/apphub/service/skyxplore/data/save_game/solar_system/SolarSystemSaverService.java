package com.github.saphyra.apphub.service.skyxplore.data.save_game.solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.GameItemSaver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class SolarSystemSaverService implements GameItemSaver {
    private final SolarSystemDao solarSystemDao;
    private final SolarSystemValidator solarSystemValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        solarSystemDao.deleteByGameId(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.SOLAR_SYSTEM;
    }

    @Override
    public void save(GameItem gameItem) {
        if (!(gameItem instanceof SolarSystemModel)) {
            throw new IllegalArgumentException("GameItem is not a " + getType() + ", it is " + gameItem.getType());
        }

        SolarSystemModel model = (SolarSystemModel) gameItem;
        solarSystemValidator.validate(model);

        solarSystemDao.save(model);
    }
}
