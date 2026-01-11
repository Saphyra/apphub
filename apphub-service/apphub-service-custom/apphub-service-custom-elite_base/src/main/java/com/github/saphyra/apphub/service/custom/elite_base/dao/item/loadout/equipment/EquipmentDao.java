package com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.equipment;

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
//TODO unit test
public class EquipmentDao extends ListCachedBufferedDao<EquipmentEntity, Equipment, ItemEntityId, Long, ItemDomainId, EquipmentRepository> implements LoadoutDao {
    private final UuidConverter uuidConverter;

    EquipmentDao(
        EquipmentConverter converter,
        EquipmentRepository repository,
        Cache<Long, List<Equipment>> readCache,
        EquipmentWriteBuffer writeBuffer,
        EquipmentDeleteBuffer deleteBuffer,
        UuidConverter uuidConverter
    ) {
        super(converter, repository, readCache, writeBuffer, deleteBuffer);
        this.uuidConverter = uuidConverter;
    }

    @Override
    protected Long getCacheKey(Equipment equipment) {
        return equipment.getMarketId();
    }

    @Override
    protected ItemDomainId toDomainId(ItemEntityId itemEntityId) {
        return ItemDomainId.builder()
            .externalReference(uuidConverter.convertEntity(itemEntityId.getExternalReference()))
            .itemName(itemEntityId.getItemName())
            .build();
    }

    @Override
    protected ItemDomainId getDomainId(Equipment equipment) {
        return ItemDomainId.builder()
            .externalReference(equipment.getExternalReference())
            .itemName(equipment.getItemName())
            .build();
    }

    @Override
    protected boolean matchesWithId(ItemDomainId domainId, Equipment equipment) {
        return Objects.equals(domainId.getExternalReference(), equipment.getExternalReference())
            && Objects.equals(domainId.getItemName(), equipment.getItemName());
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
