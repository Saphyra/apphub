package com.github.saphyra.apphub.service.skyxplore.data.save_game.alliance;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllianceModel;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AllianceDao extends AbstractDao<AllianceEntity, AllianceModel, String, AllianceRepository> {
    private final UuidConverter uuidConverter;

    public AllianceDao(AllianceConverter converter, AllianceRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }
}
