package com.github.saphyra.apphub.service.custom.elite_base.dao.last_update;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.dao.CachedDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

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

    public LastUpdate findByIdValidated(UUID externalReference, EntityType entityType) {
        LastUpdateId id = LastUpdateId.builder()
            .externalReference(uuidConverter.convertDomain(externalReference))
            .type(entityType)
            .build();
        return findById(id)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(
                HttpStatus.NOT_FOUND,
                ErrorCode.DATA_NOT_FOUND,
                "LastUpdate not found by externalReference %s and type %s".formatted(externalReference, entityType)
            ));
    }
}
