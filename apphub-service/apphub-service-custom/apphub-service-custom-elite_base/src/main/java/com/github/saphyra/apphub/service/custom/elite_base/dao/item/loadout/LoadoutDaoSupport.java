package com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.equipment.EquipmentDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.equipment.EquipmentFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.spaceship.SpaceshipDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.spaceship.SpaceshipFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class LoadoutDaoSupport {
    private final Map<ItemType, LoadoutDao> daos;
    private final EquipmentFactory equipmentFactory;
    private final SpaceshipFactory spaceshipFactory;

    public LoadoutDaoSupport(EquipmentDao equipmentDao, SpaceshipDao spaceshipDao, EquipmentFactory equipmentFactory, SpaceshipFactory spaceshipFactory) {
        this.equipmentFactory = equipmentFactory;
        this.spaceshipFactory = spaceshipFactory;
        this.daos = Map.of(
            ItemType.EQUIPMENT, equipmentDao,
            ItemType.SPACESHIP, spaceshipDao
        );
    }

    public List<Loadout> getByMarketId(ItemType type, Long marketId) {
        return cast(getDao(type).getByMarketId(marketId));
    }

    private LoadoutDao getDao(ItemType type) {
        return Optional.ofNullable(daos.get(type))
            .orElseThrow(() -> createTypeNotSupportedException(type));
    }

    private static IllegalArgumentException createTypeNotSupportedException(ItemType type) {
        return new IllegalArgumentException(type + " is not a Loadout item type.");
    }

    @SuppressWarnings("unchecked")
    private List<Loadout> cast(List<? extends Loadout> list) {
        return (List<Loadout>) list;
    }

    public Loadout create(ItemType type, ItemLocationType locationType, UUID externalReference, Long marketId, String name) {
        return switch (type) {
            case EQUIPMENT -> equipmentFactory.create(locationType, externalReference, marketId, name);
            case SPACESHIP -> spaceshipFactory.create(locationType, externalReference, marketId, name);
            default -> throw createTypeNotSupportedException(type);
        };
    }

    public void deleteAll(ItemType type, List<Loadout> loadouts) {
        getDao(type)
            .deleteAllLoadout(loadouts);
    }

    public void saveAll(ItemType type, List<Loadout> loadouts) {
        getDao(type)
            .saveAllLoadout(loadouts);
    }
}
