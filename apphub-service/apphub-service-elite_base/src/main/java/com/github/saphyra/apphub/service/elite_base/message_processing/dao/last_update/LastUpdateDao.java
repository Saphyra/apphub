package com.github.saphyra.apphub.service.elite_base.message_processing.dao.last_update;

import com.github.saphyra.apphub.lib.common_util.CachedDao;
import com.github.saphyra.apphub.lib.common_util.converter.Converter;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
//TODO unit test
public class LastUpdateDao extends CachedDao<LastUpdateEntity, LastUpdate, LastUpdateId, LastUpdateRepository> {
    private final UuidConverter uuidConverter;

    LastUpdateDao(Converter<LastUpdateEntity, LastUpdate> converter, LastUpdateRepository repository, UuidConverter uuidConverter, LastUpdateCache cache) {
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
