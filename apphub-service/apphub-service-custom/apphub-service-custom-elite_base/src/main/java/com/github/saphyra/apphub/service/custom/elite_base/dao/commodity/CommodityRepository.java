package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface CommodityRepository extends CrudRepository<CommodityEntity, CommodityEntityId> {
    List<CommodityEntity> getByIdExternalReferenceOrMarketId(String externalReference, Long marketId);
}
