package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.loadout.equipment;

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
public class EquipmentDao extends ListCachedBufferedDao<EquipmentEntity, Equipment, String, Long, ItemDomainId, EquipmentRepository> implements LoadoutDao {
    EquipmentDao(
        EquipmentConverter converter,
        EquipmentRepository repository,
        Cache<Long, List<Equipment>> readCache,
        EquipmentWriteBuffer writeBuffer,
        EquipmentDeleteBuffer deleteBuffer
    ) {
        super(converter, repository, readCache, writeBuffer, deleteBuffer);
    }

    @Override
    protected Long getCacheKey(Equipment equipment) {
        return equipment.getMarketId();
    }

    @Override
    protected ItemDomainId toDomainId(String id) {
        return repository.findById(id)
            .map(converter::convertEntity)
            .map(this::getDomainId)
            .orElseGet(() -> ItemDomainId.builder().entityId(id).build());
    }

    @Override
    protected ItemDomainId getDomainId(Equipment equipment) {
        return ItemDomainId.builder()
            .externalReference(equipment.getExternalReference())
            .itemName(equipment.getItemName())
            .locationType(equipment.getLocationType())
            .build();
    }

    @Override
    protected boolean matchesWithId(ItemDomainId domainId, Equipment equipment) {
        return Objects.equals(domainId.getExternalReference(), equipment.getExternalReference())
            && Objects.equals(domainId.getItemName(), equipment.getItemName())
            && Objects.equals(domainId.getLocationType(), equipment.getLocationType());
    }

    @Override
    public List<? extends Loadout> getByMarketId(Long marketId) {
        return searchList(marketId, () -> repository.getByMarketId(marketId));
    }

    @Override
    public void deleteAllLoadout(List<Loadout> loadouts) {
        List<Equipment> equipment = loadouts.stream()
            .map(tradeable -> (Equipment) tradeable)
            .toList();
        deleteAll(equipment);
    }

    @Override
    public void saveAllLoadout(List<Loadout> loadouts) {
        List<Equipment> equipment = loadouts.stream()
            .map(tradeable -> (Equipment) tradeable)
            .toList();
        saveAll(equipment);
    }
}
