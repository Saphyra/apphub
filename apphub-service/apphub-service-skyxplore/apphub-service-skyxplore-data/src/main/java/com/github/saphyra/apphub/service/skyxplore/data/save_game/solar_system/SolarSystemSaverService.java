package com.github.saphyra.apphub.service.skyxplore.data.save_game.solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
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
public class SolarSystemSaverService implements GameItemSaver {
    private final SolarSystemDao solarSystemDao;
    private final SolarSystemModelValidator solarSystemModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        solarSystemDao.deleteByGameId(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.SOLAR_SYSTEM;
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<SolarSystemModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof SolarSystemModel)
            .map(gameItem -> (SolarSystemModel) gameItem)
            .peek(solarSystemModelValidator::validate)
            .collect(Collectors.toList());


        solarSystemDao.saveAll(models);
    }
}
