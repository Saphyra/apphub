package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.detail;

import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrier;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrierDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.Station;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.StationDao;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.Offer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class LocationDataLoaderTest {
    private static final UUID STATION_ID = UUID.randomUUID();
    private static final UUID FLEET_CARRIER_ID = UUID.randomUUID();

    @Spy
    private ExecutorServiceBean executorServiceBean = ExecutorServiceBeenTestUtils.create(mock(ErrorReporterService.class));

    @Mock
    private StationDao stationDao;

    @Mock
    private FleetCarrierDao fleetCarrierDao;

    @InjectMocks
    private LocationDataLoader underTest;

    @Mock
    private Offer stationOffer;

    @Mock
    private Offer fleetCarrierOffer;

    @Mock
    private Station station;

    @Mock
    private FleetCarrier fleetCarrier;

    @Test
    void loadLocationData() {
        given(stationOffer.getLocationType()).willReturn(ItemLocationType.STATION);
        given(fleetCarrierOffer.getLocationType()).willReturn(ItemLocationType.FLEET_CARRIER);
        given(stationOffer.getExternalReference()).willReturn(STATION_ID);
        given(fleetCarrierOffer.getExternalReference()).willReturn(FLEET_CARRIER_ID);
        given(stationDao.findAllById(List.of(STATION_ID))).willReturn(List.of(station));
        given(fleetCarrierDao.findAllById(List.of(FLEET_CARRIER_ID))).willReturn(List.of(fleetCarrier));
        given(station.getId()).willReturn(STATION_ID);
        given(fleetCarrier.getId()).willReturn(FLEET_CARRIER_ID);

        assertThat(underTest.loadLocationData(List.of(stationOffer, fleetCarrierOffer)))
            .hasSize(2)
            .containsEntry(STATION_ID, station)
            .containsEntry(FLEET_CARRIER_ID, fleetCarrier);
    }
}