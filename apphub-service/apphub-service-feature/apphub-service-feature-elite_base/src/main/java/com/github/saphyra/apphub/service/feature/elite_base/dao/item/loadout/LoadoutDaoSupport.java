package com.github.saphyra.apphub.service.feature.elite_base.dao.item.loadout;

import com.github.saphyra.apphub.service.feature.elite_base.dao.item.ItemLocationType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.loadout.equipment.EquipmentDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.loadout.equipment.EquipmentFactory;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.loadout.spaceship.SpaceshipDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.loadout.spaceship.SpaceshipFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class LoadoutDaoSupport {
    private final Map<ObjectType, LoadoutDao> daos;
    private final EquipmentFactory equipmentFactory;
    private final SpaceshipFactory spaceshipFactory;

    public LoadoutDaoSupport(EquipmentDao equipmentDao, SpaceshipDao spaceshipDao, EquipmentFactory equipmentFactory, SpaceshipFactory spaceshipFactory) {
        this.equipmentFactory = equipmentFactory;
        this.spaceshipFactory = spaceshipFactory;
        this.daos = Map.of(
            ObjectType.EQUIPMENT, equipmentDao,
            ObjectType.SPACESHIP, spaceshipDao
        );
    }

    public List<Loadout> getByMarketId(ObjectType type, Long marketId) {
        return cast(getDao(type).getByMarketId(marketId));
    }

    private LoadoutDao getDao(ObjectType type) {
        return Optional.ofNullable(daos.get(type))
            .orElseThrow(() -> createTypeNotSupportedException(type));
    }

    private static IllegalArgumentException createTypeNotSupportedException(ObjectType type) {
        return new IllegalArgumentException(type + " is not a Loadout item type.");
    }

    @SuppressWarnings("unchecked")
    private List<Loadout> cast(List<? extends Loadout> list) {
        return (List<Loadout>) list;
    }

    public Loadout create(ObjectType type, ItemLocationType locationType, UUID externalReference, Long marketId, String name, UUID starSystemId) {
        return switch (type) {
            case EQUIPMENT -> equipmentFactory.create(locationType, externalReference, marketId, name, starSystemId);
            case SPACESHIP -> spaceshipFactory.create(locationType, externalReference, marketId, name, starSystemId);
            default -> throw createTypeNotSupportedException(type);
        };
    }

    public void deleteAll(ObjectType type, List<Loadout> loadouts) {
        getDao(type)
            .deleteAllLoadout(loadouts);
    }

    public void saveAll(ObjectType type, List<Loadout> loadouts) {
        getDao(type)
            .saveAllLoadout(loadouts);
    }
}
