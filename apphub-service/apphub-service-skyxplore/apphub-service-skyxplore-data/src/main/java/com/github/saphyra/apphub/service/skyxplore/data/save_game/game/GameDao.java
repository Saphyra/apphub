package com.github.saphyra.apphub.service.skyxplore.data.save_game.game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
//TODO unit test
public class GameDao extends AbstractDao<GameEntity, GameModel, String, GameRepository> {
    private final UuidConverter uuidConverter;

    public GameDao(GameConverter converter, GameRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteById(UUID gameId) {
        repository.deleteById(uuidConverter.convertDomain(gameId));
    }
}