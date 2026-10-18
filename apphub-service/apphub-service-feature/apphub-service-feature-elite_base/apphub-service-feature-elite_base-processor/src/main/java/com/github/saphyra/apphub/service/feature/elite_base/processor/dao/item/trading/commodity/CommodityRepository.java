package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.trading.commodity;

import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.ItemEntityId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface CommodityRepository extends CrudRepository<CommodityEntity, ItemEntityId> {
    List<CommodityEntity> getByMarketId(Long marketId);
}
