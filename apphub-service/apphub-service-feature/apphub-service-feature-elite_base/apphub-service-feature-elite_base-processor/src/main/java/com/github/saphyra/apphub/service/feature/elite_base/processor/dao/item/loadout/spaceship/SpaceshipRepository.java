package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.loadout.spaceship;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
interface SpaceshipRepository extends CrudRepository<SpaceshipEntity, String> {
    List<SpaceshipEntity> getByMarketId(Long marketId);

    List<SpaceshipEntity> getByStationIdAndItemName(String stationId, String itemName);

    List<SpaceshipEntity> getByFleetCarrierIdAndItemName(String fleetCarrierId, String itemName);

    List<SpaceshipEntity> getByStationIdInAndItemNameIn(List<String> stationIds, List<String> itemNames);

    List<SpaceshipEntity> getByFleetCarrierIdInAndItemNameIn(List<String> fleetCarrierIds, List<String> itemNames);
}
