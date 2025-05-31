package com.github.saphyra.apphub.service.custom.elite_base.dao.loadout;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class LoadoutDao extends AbstractDao<LoadoutEntity, Loadout, LoadoutEntityId, LoadoutRepository> {
    private final UuidConverter uuidConverter;

    LoadoutDao(LoadoutConverter converter, LoadoutRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public List<Loadout> getByExternalReferenceOrMarketIdAndLoadoutType(UUID externalReference, Long marketId, LoadoutType loadoutType) {
        return converter.convertEntity(repository.getByExternalReferenceOrMarketIdAndLoadoutType(uuidConverter.convertDomain(externalReference), marketId, loadoutType));
    }
}
