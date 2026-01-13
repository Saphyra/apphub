package com.github.saphyra.apphub.service.custom.elite_base.dao.last_update;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.CachedDao;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
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

    public LastUpdate findByIdValidated(UUID externalReference, ItemType itemType) {
        return findById(externalReference, itemType)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(
                HttpStatus.NOT_FOUND,
                ErrorCode.DATA_NOT_FOUND,
                "LastUpdate not found by externalReference %s and type %s".formatted(externalReference, itemType)
            ));
    }

    //TODO unit test
    public Optional<LastUpdate> findById(UUID externalReference, ItemType type) {
        LastUpdateId id = LastUpdateId.builder()
            .externalReference(uuidConverter.convertDomain(externalReference))
            .type(type)
            .build();
        return findById(id);
    }

    //TODO unit test
    public List<LastUpdate> getLastUpdates(ItemType itemType, List<UUID> externalReferences) {
        List<LastUpdateId> ids = externalReferences.stream()
            .map(externalReference -> LastUpdateId.builder()
                .externalReference(uuidConverter.convertDomain(externalReference))
                .type(itemType)
                .build())
            .toList();

        return findAllById(ids);
    }

    //TODO unit test
    public List<LastUpdate> findAllById(Map<UUID, ItemType> lastUpdateIdMap) {
        List<LastUpdateId> ids = lastUpdateIdMap.entrySet().stream()
            .map(entry -> LastUpdateId.builder()
                .externalReference(uuidConverter.convertDomain(entry.getKey()))
                .type(entry.getValue())
                .build())
            .toList();

        return findAllById(ids);
    }
}
