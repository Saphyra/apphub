package com.github.saphyra.apphub.service.elite_base.dao.loadout;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
//TODO unit test
public class LoadoutDao extends AbstractDao<LoadoutEntity, Loadout, LoadoutEntityId, LoadoutRepository> {
    private final UuidConverter uuidConverter;

    LoadoutDao(LoadoutConverter converter, LoadoutRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public List<Loadout> getByExternalReferenceOrMarketId(UUID externalReference, Long marketId) {
        return converter.convertEntity(repository.getByExternalReferenceOrMarketId(uuidConverter.convertDomain(externalReference), marketId));
    }
}
