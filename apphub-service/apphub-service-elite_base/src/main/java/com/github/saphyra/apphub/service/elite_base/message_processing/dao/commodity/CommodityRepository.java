package com.github.saphyra.apphub.service.elite_base.message_processing.dao.commodity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface CommodityRepository extends CrudRepository<CommodityEntity, String> {
    List<CommodityEntity> getByExternalReferenceOrMarketId(String externalReference, Long marketId);
}
