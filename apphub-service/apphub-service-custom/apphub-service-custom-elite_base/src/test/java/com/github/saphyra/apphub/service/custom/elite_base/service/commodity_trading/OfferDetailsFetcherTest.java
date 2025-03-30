package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import com.github.saphyra.apphub.service.custom.elite_base.dao.StationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.Body;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.BodyDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.Commodity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrier;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrierDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystemDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.StarSystemData;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.StarSystemDataDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.Station;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.StationDao;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class OfferDetailsFetcherTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID BODY_ID = UUID.randomUUID();
    private static final String STATION_NAME = "station-name";
    private static final String CARRIER_NAME = "carrier-name";
    private static final String CARRIER_ID = "carrier-id";

    @Mock
    private StarSystemDao starSystemDao;

    @Mock
    private StationDao stationDao;

    @Mock
    private StarSystemDataDao starSystemDataDao;

    @Mock
    private BodyDao bodyDao;

    @Mock
    private FleetCarrierDao fleetCarrierDao;

    @Mock
    private OfferMapper offerMapper;

    @InjectMocks
    private OfferDetailsFetcher underTest;

    @Mock
    private Commodity commodity;

    @Mock
    private CommodityTradingResponse response;

    @Mock
    private StarSystem referenceSystem;

    @Mock
    private Station station;

    @Mock
    private StarSystem starSystem;

    @Mock
    private StarSystemData starSystemData;

    @Mock
    private Body body;

    @Mock
    private FleetCarrier fleetCarrier;

    @Captor
    private ArgumentCaptor<Map<UUID, CommodityLocationData>> argumentCaptor;

    @Test
    void excludeFleetCarriers() {
        given(commodity.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(stationDao.findAllById(List.of(EXTERNAL_REFERENCE))).willReturn(List.of(station));
        given(station.getId()).willReturn(EXTERNAL_REFERENCE);
        given(station.getStarSystemId()).willReturn(STAR_SYSTEM_ID);
        given(station.getBodyId()).willReturn(BODY_ID);
        given(station.getStationName()).willReturn(STATION_NAME);
        given(station.getType()).willReturn(StationType.SURFACE_STATION);
        given(starSystemDao.findAllById(List.of(STAR_SYSTEM_ID))).willReturn(List.of(starSystem));
        given(starSystem.getId()).willReturn(STAR_SYSTEM_ID);
        given(starSystemDataDao.findAllById(List.of(STAR_SYSTEM_ID))).willReturn(List.of(starSystemData));
        given(starSystemData.getStarSystemId()).willReturn(STAR_SYSTEM_ID);
        given(bodyDao.findAllById(List.of(BODY_ID))).willReturn(List.of(body));
        given(body.getId()).willReturn(BODY_ID);

        given(offerMapper.mapOffer(
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any()
        ))
            .willReturn(Optional.of(response));

        assertThat(underTest.assembleResponses(TradeMode.SELL, referenceSystem, List.of(commodity), false)).containsExactly(response);

        then(offerMapper).should().mapOffer(
            eq(TradeMode.SELL),
            eq(referenceSystem),
            argumentCaptor.capture(),
            eq(Map.of(STAR_SYSTEM_ID, starSystem)),
            eq(Map.of(STAR_SYSTEM_ID, starSystemData)),
            eq(Map.of(BODY_ID, body)),
            eq(commodity)
        );

        CustomAssertions.singleMapAssert(argumentCaptor.getValue(), EXTERNAL_REFERENCE)
            .returns(EXTERNAL_REFERENCE, CommodityLocationData::getExternalReference)
            .returns(STAR_SYSTEM_ID, CommodityLocationData::getStarSystemId)
            .returns(BODY_ID, CommodityLocationData::getBodyId)
            .returns(STATION_NAME, CommodityLocationData::getLocationName)
            .returns(StationType.SURFACE_STATION, CommodityLocationData::getStationType);

        then(fleetCarrierDao).shouldHaveNoInteractions();
    }

    @Test
    void includeFleetCarriers() {
        given(commodity.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(stationDao.findAllById(List.of(EXTERNAL_REFERENCE))).willReturn(Collections.emptyList());
        given(fleetCarrierDao.findAllById(List.of(EXTERNAL_REFERENCE))).willReturn(List.of(fleetCarrier));
        given(fleetCarrier.getId()).willReturn(EXTERNAL_REFERENCE);
        given(fleetCarrier.getStarSystemId()).willReturn(STAR_SYSTEM_ID);
        given(fleetCarrier.getCarrierName()).willReturn(CARRIER_NAME);
        given(fleetCarrier.getCarrierId()).willReturn(CARRIER_ID);
        given(starSystemDao.findAllById(List.of(STAR_SYSTEM_ID))).willReturn(List.of(starSystem));
        given(starSystem.getId()).willReturn(STAR_SYSTEM_ID);
        given(starSystemDataDao.findAllById(List.of(STAR_SYSTEM_ID))).willReturn(List.of(starSystemData));
        given(starSystemData.getStarSystemId()).willReturn(STAR_SYSTEM_ID);

        given(offerMapper.mapOffer(
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any()
        ))
            .willReturn(Optional.of(response));

        assertThat(underTest.assembleResponses(TradeMode.SELL, referenceSystem, List.of(commodity), true)).containsExactly(response);

        then(offerMapper).should().mapOffer(
            eq(TradeMode.SELL),
            eq(referenceSystem),
            argumentCaptor.capture(),
            eq(Map.of(STAR_SYSTEM_ID, starSystem)),
            eq(Map.of(STAR_SYSTEM_ID, starSystemData)),
            eq(Map.of()),
            eq(commodity)
        );

        CustomAssertions.singleMapAssert(argumentCaptor.getValue(), EXTERNAL_REFERENCE)
            .returns(EXTERNAL_REFERENCE, CommodityLocationData::getExternalReference)
            .returns(STAR_SYSTEM_ID, CommodityLocationData::getStarSystemId)
            .returns(null, CommodityLocationData::getBodyId)
            .returns(String.join(" - ", CARRIER_ID, CARRIER_NAME), CommodityLocationData::getLocationName)
            .returns(StationType.FLEET_CARRIER, CommodityLocationData::getStationType);
    }
}