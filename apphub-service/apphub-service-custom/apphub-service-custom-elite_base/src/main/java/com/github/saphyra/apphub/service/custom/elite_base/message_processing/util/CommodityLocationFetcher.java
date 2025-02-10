package com.github.saphyra.apphub.service.custom.elite_base.message_processing.util;

import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityLocation;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrierDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.StationDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommodityLocationFetcher {
    private final StationDao stationDao;
    private final FleetCarrierDao fleetCarrierDao;

    public CommodityLocation fetchCommodityLocationByMarketId(Long marketId) {
        if (stationDao.findByMarketId(marketId).isPresent()) {
            return CommodityLocation.STATION;
        }

        if (fleetCarrierDao.findByMarketId(marketId).isPresent()) {
            return CommodityLocation.FLEET_CARRIER;
        }

        return CommodityLocation.UNKNOWN;
    }
}
