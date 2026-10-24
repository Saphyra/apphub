package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.loadout.equipment;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
interface EquipmentRepository extends CrudRepository<EquipmentEntity, String> {
    List<EquipmentEntity> getByMarketId(Long marketId);

    List<EquipmentEntity> getByStationIdAndItemName(String stationId, String itemName);

    List<EquipmentEntity> getByFleetCarrierIdAndItemName(String fleetCarrierId, String itemName);

    List<EquipmentEntity> getByStationIdInAndItemNameIn(List<String> stationIds, List<String> itemNames);

    List<EquipmentEntity> getByFleetCarrierIdInAndItemNameIn(List<String> fleetCarrierIds, List<String> itemNames);
}
