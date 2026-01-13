package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.Relation;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.Power;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.OfferDetail;
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
    private OfferDetail offerDetail;

    @Mock
    private CommodityTradingRequest request;

    @Test
    void matches() {
        given(request.getControllingPowerRelation()).willReturn(Relation.ANY);

        assertThat(underTest.matches(offerDetail, request)).isTrue();
    }

    @Test
    void doesNotMatch() {
        given(offerDetail.getControllingPower()).willReturn(Power.NAKATO_KAINE);
        given(request.getControllingPowerRelation()).willReturn(Relation.EMPTY);

        assertThat(underTest.matches(offerDetail, request)).isFalse();
    }
}