package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import com.github.saphyra.apphub.api.custom.elite_base.model.LandingPad;
import com.github.saphyra.apphub.lib.geometry.n_dimension.NDimensionCoordinate;
import com.github.saphyra.apphub.lib.geometry.n_dimension.NDimensionDistanceCalculator;
import com.github.saphyra.apphub.service.custom.elite_base.dao.StationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.Body;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.Commodity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.EntityType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdate;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystemPosition;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.Power;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.PowerplayState;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.StarSystemData;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OfferMapperTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID BODY_ID = UUID.randomUUID();
    private static final String STAR_NAME = "star-name";

    private static final Double REFERENCE_X = 1d;
    private static final Double REFERENCE_Y = 2d;
    private static final Double REFERENCE_Z = 3d;
    private static final StarSystemPosition REFERENCE_POSITION = new StarSystemPosition(REFERENCE_X, REFERENCE_Y, REFERENCE_Z);
    private static final Double STAR_SYSTEM_X = 4d;
    private static final Double STAR_SYSTEM_Y = 5d;
    private static final Double STAR_SYSTEM_Z = 6d;
    private static final StarSystemPosition STAR_SYSTEM_POSITION = new StarSystemPosition(STAR_SYSTEM_X, STAR_SYSTEM_Y, STAR_SYSTEM_Z);
    private static final Double STAR_SYSTEM_DISTANCE = 32d;
    private static final String LOCATION_NAME = "location-name";
    private static final Double STATION_DISTANCE = 42d;
    private static final Integer TRADE_AMOUNT = 3124;
    private static final Integer PRICE = 42;
    private static final LocalDateTime LAST_UPDATED = LocalDateTime.now();

    @Mock
    private NDimensionDistanceCalculator distanceCalculator;

    @Mock
    private LastUpdateDao lastUpdateDao;

    @InjectMocks
    private OfferMapper underTest;

    @Mock
    private StarSystem referenceSystem;

    @Mock
    private Commodity commodity;

    @Mock
    private CommodityLocationData commodityLocationData;

    @Mock
    private StarSystem starSystem;

    @Mock
    private StarSystemData starSystemData;

    @Mock
    private Body body;

    @Mock
    private LastUpdate lastUpdate;

    @Test
    void nullCommodityData() {
        given(commodity.getExternalReference()).willReturn(EXTERNAL_REFERENCE);

        assertThat(underTest.mapOffer(TradeMode.SELL, referenceSystem, Map.of(), Map.of(), Map.of(), Map.of(), commodity)).isEmpty();
    }

    @Test
    void nullStarSystem() {
        given(commodity.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(commodityLocationData.getStarSystemId()).willReturn(STAR_SYSTEM_ID);

        assertThat(underTest.mapOffer(
            TradeMode.SELL,
            referenceSystem,
            Map.of(EXTERNAL_REFERENCE, commodityLocationData),
            Map.of(),
            Map.of(),
            Map.of(),
            commodity
        )).isEmpty();
    }

    @Test
    void mapOffer() {
        given(commodity.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(commodityLocationData.getStarSystemId()).willReturn(STAR_SYSTEM_ID);
        given(commodityLocationData.getBodyId()).willReturn(BODY_ID);
        given(starSystem.getStarName()).willReturn(STAR_NAME);
        given(referenceSystem.getPosition()).willReturn(REFERENCE_POSITION);
        given(starSystem.getPosition()).willReturn(STAR_SYSTEM_POSITION);
        given(distanceCalculator.calculateDistance(
            new NDimensionCoordinate(REFERENCE_X, REFERENCE_Y, REFERENCE_Z),
            new NDimensionCoordinate(STAR_SYSTEM_X, STAR_SYSTEM_Y, STAR_SYSTEM_Z)
        )).willReturn(STAR_SYSTEM_DISTANCE);
        given(commodityLocationData.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(commodityLocationData.getLocationName()).willReturn(LOCATION_NAME);
        given(commodityLocationData.getStationType()).willReturn(StationType.OCELLUS);
        given(body.getDistanceFromStar()).willReturn(STATION_DISTANCE);
        given(commodity.getDemand()).willReturn(TRADE_AMOUNT);
        given(commodity.getBuyPrice()).willReturn(PRICE);
        given(starSystemData.getControllingPower()).willReturn(Power.NAKATO_KAINE);
        given(starSystemData.getPowers()).willReturn(List.of(Power.ARCHON_DELAINE));
        given(starSystemData.getPowerplayState()).willReturn(PowerplayState.FORTIFIED);
        given(commodity.getType()).willReturn(CommodityType.COMMODITY);
        given(lastUpdateDao.findByIdValidated(EXTERNAL_REFERENCE, EntityType.COMMODITY)).willReturn(lastUpdate);
        given(lastUpdate.getLastUpdate()).willReturn(LAST_UPDATED);
        given(starSystem.getId()).willReturn(STAR_SYSTEM_ID);

        CustomAssertions.optionalAssertThat(underTest.mapOffer(
                TradeMode.SELL,
                referenceSystem,
                Map.of(EXTERNAL_REFERENCE, commodityLocationData),
                Map.of(STAR_SYSTEM_ID, starSystem),
                Map.of(STAR_SYSTEM_ID, starSystemData),
                Map.of(BODY_ID, body),
                commodity
            ))
            .returns(STAR_SYSTEM_ID, CommodityTradingResponse::getStarId)
            .returns(STAR_NAME, CommodityTradingResponse::getStarName)
            .returns(STAR_SYSTEM_DISTANCE, CommodityTradingResponse::getStarSystemDistance)
            .returns(EXTERNAL_REFERENCE, CommodityTradingResponse::getExternalReference)
            .returns(LOCATION_NAME, CommodityTradingResponse::getLocationName)
            .returns(StationType.OCELLUS.name(), CommodityTradingResponse::getLocationType)
            .returns(STATION_DISTANCE, CommodityTradingResponse::getStationDistance)
            .returns(LandingPad.LARGE, CommodityTradingResponse::getLandingPad)
            .returns(TRADE_AMOUNT, CommodityTradingResponse::getTradeAmount)
            .returns(PRICE, CommodityTradingResponse::getPrice)
            .returns(Power.NAKATO_KAINE.name(), CommodityTradingResponse::getControllingPower)
            .returns(List.of(Power.ARCHON_DELAINE.name()), CommodityTradingResponse::getPowers)
            .returns(PowerplayState.FORTIFIED.name(), CommodityTradingResponse::getPowerplayState)
            .returns(LAST_UPDATED, CommodityTradingResponse::getLastUpdated);
    }
}