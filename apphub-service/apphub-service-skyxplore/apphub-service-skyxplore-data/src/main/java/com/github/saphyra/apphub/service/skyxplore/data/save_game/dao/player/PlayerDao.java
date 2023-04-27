package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.player;

import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
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

    public Optional<PlayerModel> findById(UUID playerId) {
        return findById(uuidConverter.convertDomain(playerId));
    }

    public void deleteById(UUID playerId) {
        deleteById(uuidConverter.convertDomain(playerId));
    }

    public List<PlayerModel> getPageByGameId(UUID gameId, Integer page, Integer itemsPerPage) {
        return converter.convertEntity(repository.getByGameId(uuidConverter.convertDomain(gameId), PageRequest.of(page, itemsPerPage)));
    }
}
