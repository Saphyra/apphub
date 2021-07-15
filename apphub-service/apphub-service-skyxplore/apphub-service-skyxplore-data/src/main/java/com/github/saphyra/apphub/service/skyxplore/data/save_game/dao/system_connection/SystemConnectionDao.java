package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.system_connection;

import com.github.saphyra.apphub.api.skyxplore.model.game.SystemConnectionModel;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class SystemConnectionDao extends AbstractDao<SystemConnectionEntity, SystemConnectionModel, String, SystemConnectionRepository> {
    private final UuidConverter uuidConverter;

    public SystemConnectionDao(SystemConnectionConverter converter, SystemConnectionRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }

    public Optional<SystemConnectionModel> findById(UUID systemConnectionId) {
        return findById(uuidConverter.convertDomain(systemConnectionId));
    }

    public List<SystemConnectionModel> getByGameId(UUID gameId) {
        return converter.convertEntity(repository.getByGameId(uuidConverter.convertDomain(gameId)));
    }
}
