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
class SystemDistanceOfferFilterTest {
    private static final Integer MAX_SYSTEM_DISTANCE = 234;

    @InjectMocks
    private SystemDistanceOfferFilter underTest;

    @Mock
    private OfferDetail offerDetail;

    @Mock
    private CommodityTradingRequest request;

    @Test
    void systemCloseEnough() {
        given(request.getMaxStarSystemDistance()).willReturn(MAX_SYSTEM_DISTANCE);
        given(offerDetail.getDistanceFromReferenceSystem()).willReturn((double) MAX_SYSTEM_DISTANCE);

        assertThat(underTest.filter(List.of(offerDetail), request)).containsExactly(offerDetail);
    }

    @Test
    void systemTooFar() {
        given(request.getMaxStarSystemDistance()).willReturn(MAX_SYSTEM_DISTANCE);
        given(offerDetail.getDistanceFromReferenceSystem()).willReturn((double) MAX_SYSTEM_DISTANCE + 1);

        assertThat(underTest.filter(List.of(offerDetail), request)).isEmpty();
    }
}