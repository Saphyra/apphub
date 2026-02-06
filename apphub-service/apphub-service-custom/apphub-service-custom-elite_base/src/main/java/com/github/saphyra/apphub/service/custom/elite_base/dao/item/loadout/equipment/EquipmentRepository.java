package com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.equipment;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemEntityId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface EquipmentRepository extends CrudRepository<EquipmentEntity, ItemEntityId> {
    List<EquipmentEntity> getByMarketId(Long marketId);
}
