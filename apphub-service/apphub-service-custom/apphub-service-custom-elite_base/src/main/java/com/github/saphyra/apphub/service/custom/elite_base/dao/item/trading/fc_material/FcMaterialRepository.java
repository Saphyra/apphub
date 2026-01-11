package com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.fc_material;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemEntityId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

//TODO unit test
interface FcMaterialRepository extends CrudRepository<FcMaterialEntity, ItemEntityId> {
    List<FcMaterialEntity> getByMarketId(Long marketId);

    @Query("SELECT f FROM FcMaterialEntity f WHERE f.id.itemName = :itemName AND f.stock >= :minTradeAmount AND f.sellPrice BETWEEN :minPrice AND :maxPrice")
    List<FcMaterialEntity> getSellOffers(String itemName, Integer minTradeAmount, Integer minPrice, Integer maxPrice);

    @Query("SELECT f FROM FcMaterialEntity f WHERE f.id.itemName = :itemName AND f.demand >= :minTradeAmount AND f.buyPrice BETWEEN :minPrice AND :maxPrice")
    List<FcMaterialEntity> getBuyOffers(String itemName, Integer minTradeAmount, Integer minPrice, Integer maxPrice);
}
