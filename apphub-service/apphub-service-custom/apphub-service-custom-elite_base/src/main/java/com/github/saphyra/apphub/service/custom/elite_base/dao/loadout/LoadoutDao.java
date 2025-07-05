package com.github.saphyra.apphub.service.custom.elite_base.dao.loadout;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.ListCachedBufferedDao;
import com.google.common.cache.Cache;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoadoutDao extends ListCachedBufferedDao<LoadoutEntity, Loadout, LoadoutEntityId, LoadoutCacheKey, LoadoutDomainId, LoadoutRepository> {
    private final UuidConverter uuidConverter;

    protected LoadoutDao(
        LoadoutConverter converter,
        LoadoutRepository repository,
        Cache<LoadoutCacheKey, List<Loadout>> loadoutReadCache,
        LoadoutWriteBuffer writeBuffer,
        LoadoutDeleteBuffer deleteBuffer,
        UuidConverter uuidConverter
    ) {
        super(
            converter,
            repository,
            loadoutReadCache,
            writeBuffer,
            deleteBuffer
        );
        this.uuidConverter = uuidConverter;
    }

    public List<Loadout> getByMarketIdAndType(Long marketId, LoadoutType loadoutType) {
        LoadoutCacheKey cacheKey = LoadoutCacheKey.builder()
            .marketId(marketId)
            .type(loadoutType)
            .build();

        return searchList(cacheKey, () -> repository.getByMarketIdAndType(marketId, loadoutType));
    }

    @Override
    protected LoadoutCacheKey getCacheKey(Loadout loadout) {
        return LoadoutCacheKey.builder()
            .marketId(loadout.getMarketId())
            .type(loadout.getType())
            .build();
    }

    @Override
    protected LoadoutDomainId toDomainId(LoadoutEntityId loadoutEntityId) {
        return LoadoutDomainId.builder()
            .externalReference(uuidConverter.convertEntity(loadoutEntityId.getExternalReference()))
            .type(loadoutEntityId.getType())
            .name(loadoutEntityId.getName())
            .build();
    }

    @Override
    protected LoadoutDomainId getDomainId(Loadout loadout) {
        return LoadoutDomainId.builder()
            .externalReference(loadout.getExternalReference())
            .type(loadout.getType())
            .name(loadout.getName())
            .build();
    }

    @Override
    protected boolean matchesWithId(LoadoutDomainId loadoutDomainId, Loadout loadout) {
        return loadoutDomainId.getExternalReference().equals(loadout.getExternalReference())
            && loadoutDomainId.getType() == loadout.getType()
            && loadoutDomainId.getName().equals(loadout.getName());
    }
}
