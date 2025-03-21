package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import com.github.saphyra.apphub.api.custom.elite_base.model.Relation;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.Power;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ControllingPowerOfferFilterTest {
    @InjectMocks
    private ControllingPowerOfferFilter underTest;

    @Mock
    private CommodityTradingResponse response;

    @Mock
    private CommodityTradingRequest request;

    @Test
    void matches() {
        given(response.getControllingPower()).willReturn(null);
        given(request.getControllingPowerRelation()).willReturn(Relation.ANY);

        assertThat(underTest.matches(response, request)).isTrue();
    }

    @Test
    void doesNotMatch() {
        given(response.getControllingPower()).willReturn(Power.NAKATO_KAINE.name());
        given(request.getControllingPowerRelation()).willReturn(Relation.EMPTY);

        assertThat(underTest.matches(response, request)).isFalse();
    }
}