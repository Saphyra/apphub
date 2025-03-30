package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StationDistanceOfferFilterTest {
    private static final Integer MAX_STATION_DISTANCE = 42;

    @InjectMocks
    private StationDistanceOfferFilter underTest;

    @Mock
    private CommodityTradingResponse response;

    @Mock
    private CommodityTradingRequest request;

    @Test
    void includeUnknownStationDistance() {
        given(response.getStationDistance()).willReturn(null);
        given(request.getIncludeUnknownStationDistance()).willReturn(true);

        assertThat(underTest.matches(response, request)).isTrue();
    }

    @Test
    void excludeUnknownStationDistance() {
        given(response.getStationDistance()).willReturn(null);
        given(request.getIncludeUnknownStationDistance()).willReturn(false);

        assertThat(underTest.matches(response, request)).isFalse();
    }

    @Test
    void stationCloseEnough() {
        given(request.getMaxStationDistance()).willReturn(MAX_STATION_DISTANCE);
        given(response.getStationDistance()).willReturn((double) MAX_STATION_DISTANCE);

        assertThat(underTest.matches(response, request)).isTrue();
    }

    @Test
    void stationTooFar() {
        given(request.getMaxStationDistance()).willReturn(MAX_STATION_DISTANCE);
        given(response.getStationDistance()).willReturn((double) MAX_STATION_DISTANCE + 1);

        assertThat(underTest.matches(response, request)).isFalse();
    }
}