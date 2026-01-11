package com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.commodity;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemEntityId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

//TODO unit test
interface CommodityRepository extends CrudRepository<CommodityEntity, ItemEntityId> {
    List<CommodityEntity> getByMarketId(Long marketId);

    @Query("SELECT c FROM CommodityEntity c WHERE c.id.itemName = :itemName AND c.stock >= :minTradeAmount AND c.sellPrice BETWEEN :minPrice AND :maxPrice")
    List<CommodityEntity> getSellOffers(String itemName, Integer minTradeAmount, Integer minPrice, Integer maxPrice);

    @Query("SELECT c FROM CommodityEntity c WHERE c.id.itemName = :itemName AND c.demand >= :minTradeAmount AND c.buyPrice BETWEEN :minPrice AND :maxPrice")
    List<CommodityEntity> getBuyOffers(String itemName, Integer minTradeAmount, Integer minPrice, Integer maxPrice);
}
