package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import com.github.saphyra.apphub.api.custom.elite_base.model.LandingPad;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LandingPadOfferFilterTest {
    @InjectMocks
    private LandingPadOfferFilter underTest;

    @Mock
    private CommodityTradingResponse response;

    @Mock
    private CommodityTradingRequest request;

    @Test
    void includeUnknownLandingPad() {
        given(response.getLandingPad()).willReturn(null);
        given(request.getIncludeUnknownLandingPad()).willReturn(true);

        assertThat(underTest.matches(response, request)).isTrue();
    }

    @Test
    void excludeUnknownLandingPad() {
        given(response.getLandingPad()).willReturn(null);
        given(request.getIncludeUnknownLandingPad()).willReturn(false);

        assertThat(underTest.matches(response, request)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("matchingLandingPads")
    void landingPadLargeEnough(LandingPad expected, LandingPad actual) {
        given(response.getLandingPad()).willReturn(actual);
        given(request.getMinLandingPad()).willReturn(expected);

        assertThat(underTest.matches(response, request)).isTrue();
    }

    @ParameterizedTest
    @MethodSource("smallLandingPads")
    void landingPadTooSmall(LandingPad expected, LandingPad actual) {
        given(response.getLandingPad()).willReturn(actual);
        given(request.getMinLandingPad()).willReturn(expected);

        assertThat(underTest.matches(response, request)).isFalse();
    }

    private static Stream<Arguments> matchingLandingPads() {
        return Stream.of(
            Arguments.of(LandingPad.LARGE, LandingPad.LARGE),
            Arguments.of(LandingPad.MEDIUM, LandingPad.LARGE),
            Arguments.of(LandingPad.MEDIUM, LandingPad.MEDIUM),
            Arguments.of(LandingPad.SMALL, LandingPad.SMALL),
            Arguments.of(LandingPad.SMALL, LandingPad.MEDIUM),
            Arguments.of(LandingPad.SMALL, LandingPad.LARGE)
        );
    }

    private static Stream<Arguments> smallLandingPads() {
        return Stream.of(
            Arguments.of(LandingPad.LARGE, LandingPad.MEDIUM),
            Arguments.of(LandingPad.LARGE, LandingPad.SMALL),
            Arguments.of(LandingPad.MEDIUM, LandingPad.SMALL)
        );
    }
}