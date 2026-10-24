package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.trading.commodity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
interface CommodityRepository extends CrudRepository<CommodityEntity, String> {
    List<CommodityEntity> getByMarketId(Long marketId);

    List<CommodityEntity> getByStationIdAndItemName(String stationId, String itemName);

    List<CommodityEntity> getByFleetCarrierIdAndItemName(String fleetCarrierId, String itemName);

    List<CommodityEntity> getByStationIdInAndItemNameIn(List<String> stationIds, List<String> itemNames);

    List<CommodityEntity> getByFleetCarrierIdInAndItemNameIn(List<String> fleetCarrierIds, List<String> itemNames);
}
