package com.github.saphyra.apphub.service.feature.elite_base.dao.last_update;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.CachedDao;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import com.google.common.cache.Cache;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class LastUpdateDao extends CachedDao<LastUpdateEntity, LastUpdate, LastUpdateId, LastUpdateRepository> {
    private final UuidConverter uuidConverter;

    LastUpdateDao(LastUpdateConverter converter, LastUpdateRepository repository, UuidConverter uuidConverter, Cache<LastUpdateId, LastUpdate> cache) {
        super(converter, repository, false, cache);
        this.uuidConverter = uuidConverter;
    }

    @Override
    protected LastUpdateId extractId(LastUpdate lastUpdate) {
        return LastUpdateId.builder()
            .externalReference(uuidConverter.convertDomain(lastUpdate.getExternalReference()))
            .objectType(lastUpdate.getType())
            .build();
    }

    @Override
    protected boolean shouldSave(LastUpdate lastUpdate) {
        Optional<LastUpdate> maybeLastUpdate = findById(extractId(lastUpdate));

        return maybeLastUpdate.isEmpty() || !maybeLastUpdate.get().getLastUpdate().equals(lastUpdate.getLastUpdate());
    }

    public LastUpdate findByIdValidated(UUID externalReference, ObjectType objectType) {
        return findById(externalReference, objectType)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(
                HttpStatus.NOT_FOUND,
                ErrorCode.DATA_NOT_FOUND,
                "LastUpdate not found by externalReference %s and type %s".formatted(externalReference, objectType)
            ));
    }

    public Optional<LastUpdate> findById(UUID externalReference, ObjectType type) {
        LastUpdateId id = LastUpdateId.builder()
            .externalReference(uuidConverter.convertDomain(externalReference))
            .objectType(type)
            .build();
        return findById(id);
    }
}
