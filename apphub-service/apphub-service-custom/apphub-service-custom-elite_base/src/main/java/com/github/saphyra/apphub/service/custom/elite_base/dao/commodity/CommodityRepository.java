package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface CommodityRepository extends CrudRepository<CommodityEntity, CommodityEntityId> {
    List<CommodityEntity> getByMarketIdAndType(Long marketId, CommodityType type);

    @Query("SELECT e FROM CommodityEntity e WHERE e.id.commodityName=:commodityName AND e.stock >= :stock AND e.sellPrice BETWEEN :minPrice AND :maxPrice")
    List<CommodityEntity> getSellOffers(
        @Param("commodityName") String commodityName,
        @Param("stock") Integer stock,
        @Param("minPrice") Integer minPrice,
        @Param("maxPrice") Integer maxPrice
    );

    @Query("SELECT e FROM CommodityEntity e WHERE e.id.commodityName=:commodityName AND e.demand >= :demand AND e.buyPrice BETWEEN :minPrice AND :maxPrice")
    List<CommodityEntity> getBuyOffers(
        @Param("commodityName") String commodityName,
        @Param("demand") Integer demand,
        @Param("minPrice") Integer minPrice,
        @Param("maxPrice") Integer maxPrice
    );
}
