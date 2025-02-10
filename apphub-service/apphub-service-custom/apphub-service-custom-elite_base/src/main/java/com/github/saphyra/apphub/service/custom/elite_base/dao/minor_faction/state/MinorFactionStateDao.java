package com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.state;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class MinorFactionStateDao extends AbstractDao<MinorFactionStateEntity, MinorFactionState, MinorFactionStateEntityId, MinorFactionStateRepository> {
    private final UuidConverter uuidConverter;

    MinorFactionStateDao(MinorFactionStateConverter converter, MinorFactionStateRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public List<MinorFactionState> getByMinorFactionIdAndStatus(UUID minorFactionId, StateStatus status) {
        return converter.convertEntity(repository.getByMinorFactionIdAndStatus(uuidConverter.convertDomain(minorFactionId), status));
    }

    public List<MinorFactionState> getByMinorFactionId(UUID minorFactionId) {
        return converter.convertEntity(repository.getByMinorFactionId(uuidConverter.convertDomain(minorFactionId)));
    }
}
