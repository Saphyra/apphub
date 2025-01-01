package com.github.saphyra.apphub.service.elite_base.dao.commodity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

//TODO unit test
interface CommodityRepository extends CrudRepository<CommodityEntity, String> {
    Optional<CommodityEntity> findByCommodityNameAndExternalReference(String commodityName, String externalReference);

    Optional<CommodityEntity> findByCommodityNameAndMarketId(String commodityName, Long marketId);

    List<CommodityEntity> getByExternalReferenceOrMarketId(String externalReference, Long marketId);
}
