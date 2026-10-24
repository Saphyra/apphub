package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.trading.fc_material;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
interface FcMaterialRepository extends CrudRepository<FcMaterialEntity, String> {
    List<FcMaterialEntity> getByMarketId(Long marketId);

    List<FcMaterialEntity> getByStationIdAndItemName(String stationId, String itemName);

    List<FcMaterialEntity> getByFleetCarrierIdAndItemName(String fleetCarrierId, String itemName);

    List<FcMaterialEntity> getByStationIdInAndItemNameIn(List<String> stationIds, List<String> itemNames);

    List<FcMaterialEntity> getByFleetCarrierIdInAndItemNameIn(List<String> fleetCarrierIds, List<String> itemNames);
}
