package com.github.saphyra.apphub.service.skyxplore.data.save_game.universe;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.UniverseModel;
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
    public void save(List<GameItem> gameItems) {
        List<UniverseModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof UniverseModel)
            .map(gameItem -> (UniverseModel) gameItem)
            .peek(universeModelValidator::validate)
            .collect(Collectors.toList());


        universeDao.saveAll(models);
    }
}
