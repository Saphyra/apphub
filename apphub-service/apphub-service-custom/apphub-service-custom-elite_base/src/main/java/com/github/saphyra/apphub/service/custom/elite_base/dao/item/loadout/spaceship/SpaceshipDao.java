package com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.spaceship;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.ListCachedBufferedDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemDomainId;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemEntityId;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.Loadout;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.LoadoutDao;
import com.google.common.cache.Cache;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class SpaceshipDao extends ListCachedBufferedDao<SpaceshipEntity, Spaceship, ItemEntityId, Long, ItemDomainId, SpaceshipRepository> implements LoadoutDao {
    private final UuidConverter uuidConverter;

    SpaceshipDao(
        SpaceshipConverter converter,
        SpaceshipRepository repository,
        Cache<Long, List<Spaceship>> readCache,
        SpaceshipWriteBuffer writeBuffer,
        SpaceshipDeleteBuffer deleteBuffer,
        UuidConverter uuidConverter
    ) {
        super(converter, repository, readCache, writeBuffer, deleteBuffer);
        this.uuidConverter = uuidConverter;
    }

    @Override
    protected Long getCacheKey(Spaceship spaceship) {
        return spaceship.getMarketId();
    }

    @Override
    protected ItemDomainId toDomainId(ItemEntityId itemEntityId) {
        return ItemDomainId.builder()
            .externalReference(uuidConverter.convertEntity(itemEntityId.getExternalReference()))
            .itemName(itemEntityId.getItemName())
            .build();
    }

    @Override
    protected ItemDomainId getDomainId(Spaceship spaceship) {
        return ItemDomainId.builder()
            .externalReference(spaceship.getExternalReference())
            .itemName(spaceship.getItemName())
            .build();
    }

    @Override
    protected boolean matchesWithId(ItemDomainId domainId, Spaceship spaceship) {
        return Objects.equals(domainId.getExternalReference(), spaceship.getExternalReference())
            && Objects.equals(domainId.getItemName(), spaceship.getItemName());
    }

    @Override
    public List<? extends Loadout> getByMarketId(Long marketId) {
        return searchList(marketId, () -> repository.getByMarketId(marketId));
    }

    @Override
    public void deleteAllLoadout(List<Loadout> loadouts) {
        List<Spaceship> spaceships = loadouts.stream()
            .map(tradeable -> (Spaceship) tradeable)
            .toList();
        deleteAll(spaceships);
    }

    @Override
    public void saveAllLoadout(List<Loadout> loadouts) {
        List<Spaceship> spaceships = loadouts.stream()
            .map(tradeable -> (Spaceship) tradeable)
            .toList();
        saveAll(spaceships);
    }
}
