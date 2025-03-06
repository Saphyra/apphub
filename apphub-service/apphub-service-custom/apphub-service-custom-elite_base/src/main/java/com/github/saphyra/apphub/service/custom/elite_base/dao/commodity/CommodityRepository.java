package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface CommodityRepository extends CrudRepository<CommodityEntity, CommodityEntityId> {
    List<CommodityEntity> getByIdExternalReferenceOrMarketId(String externalReference, Long marketId);

    //TODO unit test
    List<CommodityEntity> getByIdCommodityNameAndStockGreaterThanAndSellPriceBetween(String commodityName, Integer stock, Integer minPrice, Integer maxPrice);

    //TODO unit test
    List<CommodityEntity> getByIdCommodityNameAndDemandGreaterThanAndBuyPriceBetween(String commodityName, Integer demand, Integer minPrice, Integer maxPrice);
}
