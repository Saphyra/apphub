package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class OfferFilterServiceTest {
    @Mock
    private OfferFilter offerFilter;

    @Mock
    private ExpirationCache expirationCache;

    private OfferFilterService underTest;

    @Mock
    private CommodityTradingRequest request;

    @Mock
    private CommodityTradingResponse response;

    @BeforeEach
    void setUp() {
        underTest = OfferFilterService.builder()
            .filters(List.of(offerFilter, offerFilter))
            .expirationCache(expirationCache)
            .build();
    }

    @AfterEach
    void verifyCacheCleared() {
        then(expirationCache).should().remove();
    }

    @Test
    void filterOffers_matches() {
        given(offerFilter.matches(response, request)).willReturn(true);

        assertThat(underTest.filterOffers(List.of(response), request)).containsExactly(response);
    }

    @Test
    void filterOffers_doesNotMatch() {
        given(offerFilter.matches(response, request))
            .willReturn(true)
            .willReturn(false);

        assertThat(underTest.filterOffers(List.of(response), request)).isEmpty();
    }
}