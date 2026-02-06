package com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.commodity;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemEntityId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface CommodityRepository extends CrudRepository<CommodityEntity, ItemEntityId> {
    List<CommodityEntity> getByMarketId(Long marketId);
}
