package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.OfferDetail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StationDistanceOfferFilterTest {
    private static final Integer MAX_STATION_DISTANCE = 42;

    @InjectMocks
    private StationDistanceOfferFilter underTest;

    @Mock
    private OfferDetail offerDetail;

    @Mock
    private CommodityTradingRequest request;

    @Test
    void includeUnknownStationDistance() {
        given(offerDetail.getStationDistanceFromStar()).willReturn(null);
        given(request.getIncludeUnknownStationDistance()).willReturn(true);

        assertThat(underTest.filter(List.of(offerDetail), request)).containsExactly(offerDetail);
    }

    @Test
    void excludeUnknownStationDistance() {
        given(offerDetail.getStationDistanceFromStar()).willReturn(null);
        given(request.getIncludeUnknownStationDistance()).willReturn(false);

        assertThat(underTest.filter(List.of(offerDetail), request)).isEmpty();
    }

    @Test
    void stationCloseEnough() {
        given(request.getMaxStationDistance()).willReturn(MAX_STATION_DISTANCE);
        given(offerDetail.getStationDistanceFromStar()).willReturn((double) MAX_STATION_DISTANCE);

        assertThat(underTest.filter(List.of(offerDetail), request)).containsExactly(offerDetail);
    }

    @Test
    void stationTooFar() {
        given(request.getMaxStationDistance()).willReturn(MAX_STATION_DISTANCE);
        given(offerDetail.getStationDistanceFromStar()).willReturn((double) MAX_STATION_DISTANCE + 1);

        assertThat(underTest.filter(List.of(offerDetail), request)).isEmpty();
    }
}