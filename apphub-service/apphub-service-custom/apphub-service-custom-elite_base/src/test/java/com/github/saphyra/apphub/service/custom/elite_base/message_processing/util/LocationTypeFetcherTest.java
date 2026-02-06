package com.github.saphyra.apphub.service.custom.elite_base.message_processing.util;

import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrier;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrierDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.Station;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.StationDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LocationTypeFetcherTest {
    private static final Long MARKET_ID = 2342L;

    @Mock
    private StationDao stationDao;

    @Mock
    private FleetCarrierDao fleetCarrierDao;

    @InjectMocks
    private LocationTypeFetcher underTest;

    @Mock
    private Station station;

    @Mock
    private FleetCarrier fleetCarrier;

    @Test
    void station() {
        given(stationDao.findByMarketId(MARKET_ID)).willReturn(Optional.of(station));

        assertThat(underTest.fetchCommodityLocationByMarketId(MARKET_ID)).isEqualTo(ItemLocationType.STATION);
    }

    @Test
    void fleetCarrier() {
        given(stationDao.findByMarketId(MARKET_ID)).willReturn(Optional.empty());
        given(fleetCarrierDao.findByMarketId(MARKET_ID)).willReturn(Optional.of(fleetCarrier));

        assertThat(underTest.fetchCommodityLocationByMarketId(MARKET_ID)).isEqualTo(ItemLocationType.FLEET_CARRIER);
    }

    @Test
    void unknown() {
        given(stationDao.findByMarketId(MARKET_ID)).willReturn(Optional.empty());
        given(fleetCarrierDao.findByMarketId(MARKET_ID)).willReturn(Optional.empty());

        assertThat(underTest.fetchCommodityLocationByMarketId(MARKET_ID)).isEqualTo(ItemLocationType.UNKNOWN);
    }
}