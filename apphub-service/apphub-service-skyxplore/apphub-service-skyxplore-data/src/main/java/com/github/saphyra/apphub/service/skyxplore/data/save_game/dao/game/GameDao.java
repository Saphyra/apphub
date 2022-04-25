package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class GameDao extends AbstractDao<GameEntity, GameModel, String, GameRepository> {
    private final UuidConverter uuidConverter;

    public GameDao(GameConverter converter, GameRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteById(UUID gameId) {
        deleteById(uuidConverter.convertDomain(gameId));
    }

    public List<GameModel> getByHost(UUID userId) {
        return converter.convertEntity(repository.getByHost(uuidConverter.convertDomain(userId)));
    }

    public Optional<GameModel> findById(UUID gameId) {
        return findById(uuidConverter.convertDomain(gameId));
    }

    public GameModel findByIdValidated(UUID gameId) {
        return findById(gameId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.GAME_NOT_FOUND, "Game not found with id " + gameId));
    }

    public List<GameModel> getGamesMarkedForDeletion() {
        return converter.convertEntity(repository.getGamesMarkedForDeletion());
    }
}
