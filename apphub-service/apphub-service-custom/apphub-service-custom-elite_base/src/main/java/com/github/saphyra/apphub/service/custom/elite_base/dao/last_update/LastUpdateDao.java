package com.github.saphyra.apphub.service.custom.elite_base.dao.last_update;

import com.github.saphyra.apphub.lib.common_util.CachedDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LastUpdateDao extends CachedDao<LastUpdateEntity, LastUpdate, LastUpdateId, LastUpdateRepository> {
    private final UuidConverter uuidConverter;

    LastUpdateDao(LastUpdateConverter converter, LastUpdateRepository repository, UuidConverter uuidConverter, LastUpdateCache cache) {
        super(converter, repository, false, cache);
        this.uuidConverter = uuidConverter;
    }

    @Override
    protected LastUpdateId extractId(LastUpdate lastUpdate) {
        return LastUpdateId.builder()
            .externalReference(uuidConverter.convertDomain(lastUpdate.getExternalReference()))
            .type(lastUpdate.getType())
            .build();
    }

    @Override
    protected boolean shouldSave(LastUpdate lastUpdate) {
        Optional<LastUpdate> maybeLastUpdate = findById(extractId(lastUpdate));

        return maybeLastUpdate.isEmpty() || !maybeLastUpdate.get().getLastUpdate().equals(lastUpdate.getLastUpdate());
    }
}
