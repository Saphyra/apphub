package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.loadout.spaceship;

import com.github.saphyra.apphub.lib.common_util.dao.ListCachedBufferedDao;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.ItemDomainId;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.loadout.Loadout;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.loadout.LoadoutDao;
import com.google.common.cache.Cache;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class SpaceshipDao extends ListCachedBufferedDao<SpaceshipEntity, Spaceship, String, Long, ItemDomainId, SpaceshipRepository> implements LoadoutDao {
    SpaceshipDao(
        SpaceshipConverter converter,
        SpaceshipRepository repository,
        Cache<Long, List<Spaceship>> readCache,
        SpaceshipWriteBuffer writeBuffer,
        SpaceshipDeleteBuffer deleteBuffer
    ) {
        super(converter, repository, readCache, writeBuffer, deleteBuffer);
    }

    @Override
    protected Long getCacheKey(Spaceship spaceship) {
        return spaceship.getMarketId();
    }

    @Override
    protected ItemDomainId toDomainId(String id) {
        return repository.findById(id)
            .map(converter::convertEntity)
            .map(this::getDomainId)
            .orElseGet(() -> ItemDomainId.builder().entityId(id).build());
    }

    @Override
    protected ItemDomainId getDomainId(Spaceship spaceship) {
        return ItemDomainId.builder()
            .externalReference(spaceship.getExternalReference())
            .itemName(spaceship.getItemName())
            .locationType(spaceship.getLocationType())
            .build();
    }

    @Override
    protected boolean matchesWithId(ItemDomainId domainId, Spaceship spaceship) {
        return Objects.equals(domainId.getExternalReference(), spaceship.getExternalReference())
            && Objects.equals(domainId.getItemName(), spaceship.getItemName())
            && Objects.equals(domainId.getLocationType(), spaceship.getLocationType());
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
