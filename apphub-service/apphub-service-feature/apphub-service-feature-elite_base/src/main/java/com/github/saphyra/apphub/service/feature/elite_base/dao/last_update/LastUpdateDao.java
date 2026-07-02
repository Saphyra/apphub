package com.github.saphyra.apphub.service.feature.elite_base.dao.last_update;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.CachedDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import com.google.common.cache.Cache;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class LastUpdateDao extends CachedDao<LastUpdateEntity, LastUpdate, LastUpdateId, LastUpdateRepository> {
    private final UuidConverter uuidConverter;
    private final LastUpdateFactory lastUpdateFactory;

    LastUpdateDao(LastUpdateConverter converter, LastUpdateRepository repository, UuidConverter uuidConverter, Cache<LastUpdateId, LastUpdate> cache, LastUpdateFactory lastUpdateFactory) {
        super(converter, repository, false, cache);
        this.uuidConverter = uuidConverter;
        this.lastUpdateFactory = lastUpdateFactory;
    }

    @Override
    protected LastUpdateId extractId(LastUpdate lastUpdate) {
        return LastUpdateId.builder()
            .externalReference(lastUpdate.getExternalReference())
            .objectType(lastUpdate.getType())
            .build();
    }

    @Override
    protected boolean shouldSave(LastUpdate lastUpdate) {
        Optional<LastUpdate> maybeLastUpdate = findById(extractId(lastUpdate));

        return maybeLastUpdate.isEmpty() || !maybeLastUpdate.get().getLastUpdate().equals(lastUpdate.getLastUpdate());
    }

    public LastUpdate findByIdOrDefault(UUID externalReference, ObjectType objectType) {
        return findByIdOrDefault(uuidConverter.convertDomain(externalReference), objectType);
    }

    public LastUpdate findByIdOrDefault(String externalReference, ObjectType objectType) {
        return findById(externalReference, objectType)
            .orElseGet(() -> lastUpdateFactory.create(externalReference, objectType));
    }

    public Optional<LastUpdate> findById(UUID externalReference, ObjectType type) {
        return findById(uuidConverter.convertDomain(externalReference), type);
    }

    public Optional<LastUpdate> findById(String externalReference, ObjectType type) {
        LastUpdateId id = LastUpdateId.builder()
            .externalReference(externalReference)
            .objectType(type)
            .build();
        return findById(id);
    }
}
