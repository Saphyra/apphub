package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ExpirationCacheTest {
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private ExpirationCache underTest;

    @Mock
    private CommodityTradingRequest request;

    @Test
    void getExpiration(){
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(request.getMaxTimeSinceLastUpdated()).willReturn(Duration.ofSeconds(3));

        assertThat(underTest.getExpiration(request)).isEqualTo(CURRENT_TIME.minusSeconds(3));
        assertThat(underTest.getExpiration(request)).isEqualTo(CURRENT_TIME.minusSeconds(3));

        then(dateTimeUtil).should().getCurrentDateTime();
        then(dateTimeUtil).shouldHaveNoMoreInteractions();
    }
}