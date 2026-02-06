package com.github.saphyra.apphub.service.custom.elite_base.message_processing.util;

import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrierDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.StationDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocationTypeFetcher {
    private final StationDao stationDao;
    private final FleetCarrierDao fleetCarrierDao;

    public ItemLocationType fetchCommodityLocationByMarketId(Long marketId) {
        if (stationDao.findByMarketId(marketId).isPresent()) {
            return ItemLocationType.STATION;
        }

        if (fleetCarrierDao.findByMarketId(marketId).isPresent()) {
            return ItemLocationType.FLEET_CARRIER;
        }

        return ItemLocationType.UNKNOWN;
    }
}
