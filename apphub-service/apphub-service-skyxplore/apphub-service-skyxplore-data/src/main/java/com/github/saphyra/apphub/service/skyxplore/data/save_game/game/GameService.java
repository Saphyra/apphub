package com.github.saphyra.apphub.service.skyxplore.data.save_game.game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.GameItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameService implements GameItemService {
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
    public void save(List<GameItem> gameItems) {
        List<GameModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof GameModel)
            .map(gameItem -> (GameModel) gameItem)
            .peek(gameModelValidator::validate)
            .collect(Collectors.toList());


        gameDao.saveAll(models);
    }
}
