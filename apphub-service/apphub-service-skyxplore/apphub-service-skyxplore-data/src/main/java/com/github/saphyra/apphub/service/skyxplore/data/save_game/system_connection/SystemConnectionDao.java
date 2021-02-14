package com.github.saphyra.apphub.service.skyxplore.data.save_game.system_connection;

import com.github.saphyra.apphub.api.skyxplore.model.game.SystemConnectionModel;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
//TODO unit test
public class SystemConnectionDao extends AbstractDao<SystemConnectionEntity, SystemConnectionModel, String, SystemConnectionRepository> {
    private final UuidConverter uuidConverter;

    public SystemConnectionDao(SystemConnectionConverter converter, SystemConnectionRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }
}
