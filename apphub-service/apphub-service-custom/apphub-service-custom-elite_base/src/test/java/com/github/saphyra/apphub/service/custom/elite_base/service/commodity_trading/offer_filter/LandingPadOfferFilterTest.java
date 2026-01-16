package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.LandingPad;
import com.github.saphyra.apphub.service.custom.elite_base.dao.StationType;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.CommodityLocationData;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.OfferDetail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LandingPadOfferFilterTest {
    @InjectMocks
    private LandingPadOfferFilter underTest;

    @Mock
    private OfferDetail offerDetail;

    @Mock
    private CommodityTradingRequest request;

    @Mock
    private CommodityLocationData commodityLocationData;

    @Test
    void includeUnknownLandingPad() {
        given(offerDetail.getCommodityLocationData()).willReturn(commodityLocationData);
        given(commodityLocationData.getStationType()).willReturn(null);
        given(request.getIncludeUnknownLandingPad()).willReturn(true);

        assertThat(underTest.filter(List.of(offerDetail), request)).containsExactly(offerDetail);
    }

    @Test
    void excludeUnknownLandingPad() {
        given(offerDetail.getCommodityLocationData()).willReturn(commodityLocationData);
        given(commodityLocationData.getStationType()).willReturn(null);
        given(request.getIncludeUnknownLandingPad()).willReturn(false);

        assertThat(underTest.filter(List.of(offerDetail), request)).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("matchingLandingPads")
    void landingPadLargeEnough(StationType stationType, LandingPad minimumLandingPad) {
        given(offerDetail.getCommodityLocationData()).willReturn(commodityLocationData);
        given(commodityLocationData.getStationType()).willReturn(stationType);
        given(request.getMinLandingPad()).willReturn(minimumLandingPad);

        assertThat(underTest.filter(List.of(offerDetail), request)).containsExactly(offerDetail);
    }

    @ParameterizedTest
    @MethodSource("smallLandingPads")
    void landingPadTooSmall(StationType stationType, LandingPad minimumLandingPad) {
        given(offerDetail.getCommodityLocationData()).willReturn(commodityLocationData);
        given(commodityLocationData.getStationType()).willReturn(stationType);
        given(request.getMinLandingPad()).willReturn(minimumLandingPad);

        assertThat(underTest.filter(List.of(offerDetail), request)).isEmpty();
    }

    private static Stream<Arguments> matchingLandingPads() {
        return Stream.of(
            Arguments.of(StationType.CORIOLIS, LandingPad.MEDIUM),
            Arguments.of(StationType.CORIOLIS, LandingPad.LARGE),
            Arguments.of(StationType.OUTPOST, LandingPad.MEDIUM)
        );
    }

    private static Stream<Arguments> smallLandingPads() {
        return Stream.of(
            Arguments.of(StationType.OUTPOST, LandingPad.LARGE)
        );
    }
}