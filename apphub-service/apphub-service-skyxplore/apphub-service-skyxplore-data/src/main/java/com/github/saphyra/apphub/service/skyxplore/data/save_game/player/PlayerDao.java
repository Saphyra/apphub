package com.github.saphyra.apphub.service.skyxplore.data.save_game.player;

import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PlayerDao extends AbstractDao<PlayerEntity, PlayerModel, String, PlayerRepository> {
    private final UuidConverter uuidConverter;

    public PlayerDao(PlayerConverter converter, PlayerRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }

    public List<PlayerModel> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    public List<PlayerModel> getByGameId(UUID gameId) {
        return converter.convertEntity(repository.getByGameId(uuidConverter.convertDomain(gameId)));
    }
}
