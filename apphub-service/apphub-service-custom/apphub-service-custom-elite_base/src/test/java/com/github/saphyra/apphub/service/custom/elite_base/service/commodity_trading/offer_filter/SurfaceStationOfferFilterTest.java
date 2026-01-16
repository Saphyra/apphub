package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.service.custom.elite_base.dao.StationType;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.CommodityLocationData;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.OfferDetail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SurfaceStationOfferFilterTest {
    @InjectMocks
    private SurfaceStationOfferFilter underTest;

    @Mock
    private CommodityTradingRequest request;

    @Mock
    private OfferDetail offerDetail;

    @Mock
    private CommodityLocationData commodityLocationData;

    @Test
    void includeSurfaceStation() {
        given(request.getIncludeSurfaceStations()).willReturn(true);

        assertThat(underTest.filter(List.of(offerDetail), request)).containsExactly(offerDetail);
    }

    @ParameterizedTest
    @EnumSource(value = StationType.class, mode = EnumSource.Mode.INCLUDE, names = {"SURFACE_STATION", "ON_FOOT_SETTLEMENT"})
    void excludeSurfaceStation(StationType stationType) {
        given(offerDetail.getCommodityLocationData()).willReturn(commodityLocationData);
        given(commodityLocationData.getStationType()).willReturn(stationType);
        given(request.getIncludeSurfaceStations()).willReturn(false);

        assertThat(underTest.filter(List.of(offerDetail), request)).isEmpty();
    }

    @Test
    void unknownLocationType() {
        given(offerDetail.getCommodityLocationData()).willReturn(commodityLocationData);
        given(commodityLocationData.getStationType()).willReturn(null);
        given(request.getIncludeSurfaceStations()).willReturn(false);

        assertThat(underTest.filter(List.of(offerDetail), request)).isEmpty();
    }

    @ParameterizedTest
    @EnumSource(value = StationType.class, mode = EnumSource.Mode.EXCLUDE, names = {"SURFACE_STATION", "ON_FOOT_SETTLEMENT"})
    void includeNotSurfaceStations(StationType stationType) {
        given(offerDetail.getCommodityLocationData()).willReturn(commodityLocationData);
        given(commodityLocationData.getStationType()).willReturn(stationType);
        given(request.getIncludeSurfaceStations()).willReturn(false);

        assertThat(underTest.filter(List.of(offerDetail), request)).containsExactly(offerDetail);
    }
}