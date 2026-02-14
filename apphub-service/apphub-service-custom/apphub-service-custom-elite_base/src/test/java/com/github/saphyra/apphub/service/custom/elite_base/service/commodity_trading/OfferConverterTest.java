package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.CommodityTradingResponseItem;
import com.github.saphyra.apphub.service.custom.elite_base.dao.StationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.Power;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.PowerplayState;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.detail.OfferDetail;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OfferConverterTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final String STAR_NAME = "star-name";
    private static final Double DISTANCE_FROM_REFERENCE_SYSTEM = 123.45;
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String LOCATION_NAME = "location-name";
    private static final Double STATION_DISTANCE_FROM_STAR = 543.21;
    private static final Integer AMOUNT = 123;
    private static final Integer PRICE = 123456;
    private static final LocalDateTime LAST_UPDATED = LocalDateTime.now();


    @InjectMocks
    private OfferConverter underTest;

    @Mock
    private OfferDetail offerDetail;

    @Test
    void convert() {
        given(offerDetail.getStarSystemId()).willReturn(STAR_SYSTEM_ID);
        given(offerDetail.getStarName()).willReturn(STAR_NAME);
        given(offerDetail.getDistanceFromReferenceSystem()).willReturn(DISTANCE_FROM_REFERENCE_SYSTEM);
        given(offerDetail.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(offerDetail.getLocationName()).willReturn(LOCATION_NAME);
        given(offerDetail.getLocationType()).willReturn(ItemLocationType.STATION);
        given(offerDetail.getStationDistanceFromStar()).willReturn(Optional.of(STATION_DISTANCE_FROM_STAR));
        given(offerDetail.getStationType()).willReturn(StationType.CORIOLIS);
        given(offerDetail.getAmount()).willReturn(AMOUNT);
        given(offerDetail.getPrice()).willReturn(PRICE);
        given(offerDetail.getControllingPower()).willReturn(Optional.of(Power.AISLING_DUVAL));
        given(offerDetail.getPowers()).willReturn(Optional.of(List.of(Power.ARCHON_DELAINE)));
        given(offerDetail.getPowerplayState()).willReturn(Optional.of(PowerplayState.CONTESTED));
        given(offerDetail.getLastUpdated()).willReturn(LAST_UPDATED);

        CustomAssertions.singleListAssertThat(underTest.convert(List.of(offerDetail)))
            .returns(STAR_SYSTEM_ID, CommodityTradingResponseItem::getStarId)
            .returns(STAR_NAME, CommodityTradingResponseItem::getStarName)
            .returns(DISTANCE_FROM_REFERENCE_SYSTEM, CommodityTradingResponseItem::getStarSystemDistance)
            .returns(EXTERNAL_REFERENCE, CommodityTradingResponseItem::getExternalReference)
            .returns(LOCATION_NAME, CommodityTradingResponseItem::getLocationName)
            .returns(ItemLocationType.STATION.name(), CommodityTradingResponseItem::getLocationType)
            .returns(STATION_DISTANCE_FROM_STAR, CommodityTradingResponseItem::getStationDistance)
            .returns(StationType.CORIOLIS.getLandingPad(), CommodityTradingResponseItem::getLandingPad)
            .returns(AMOUNT, CommodityTradingResponseItem::getTradeAmount)
            .returns(PRICE, CommodityTradingResponseItem::getPrice)
            .returns(Power.AISLING_DUVAL.name(), CommodityTradingResponseItem::getControllingPower)
            .returns(List.of(Power.ARCHON_DELAINE.name()), CommodityTradingResponseItem::getPowers)
            .returns(PowerplayState.CONTESTED.name(), CommodityTradingResponseItem::getPowerplayState)
            .returns(LAST_UPDATED, CommodityTradingResponseItem::getLastUpdated);
    }
}