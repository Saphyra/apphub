package com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.spaceship;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemEntityId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface SpaceshipRepository extends CrudRepository<SpaceshipEntity, ItemEntityId> {
    List<SpaceshipEntity> getByMarketId(Long marketId);
}
