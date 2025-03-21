package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LastUpdatedOfferFilterTest {
    private static final LocalDateTime EXPIRATION = LocalDateTime.now();

    @Mock
    private ExpirationCache expirationCache;

    @InjectMocks
    private LastUpdatedOfferFilter underTest;

    @Mock
    private CommodityTradingRequest request;

    @Mock
    private CommodityTradingResponse response;

    @Test
    void valid() {
        given(expirationCache.getExpiration(request)).willReturn(EXPIRATION);
        given(response.getLastUpdated()).willReturn(EXPIRATION.plusSeconds(1));

        assertThat(underTest.matches(response, request)).isTrue();
    }

    @Test
    void expired() {
        given(expirationCache.getExpiration(request)).willReturn(EXPIRATION);
        given(response.getLastUpdated()).willReturn(EXPIRATION.minusSeconds(1));

        assertThat(underTest.matches(response, request)).isFalse();
    }
}