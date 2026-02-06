package com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.fc_material;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemEntityId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface FcMaterialRepository extends CrudRepository<FcMaterialEntity, ItemEntityId> {
    List<FcMaterialEntity> getByMarketId(Long marketId);
}
