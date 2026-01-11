package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.PowerplayState;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.OfferDetail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PowerplayStateOfferFilterTest {
    @InjectMocks
    private PowerplayStateOfferFilter underTest;

    @Mock
    private OfferDetail offerDetail;

    @Mock
    private CommodityTradingRequest request;

    @Test
    void noFiltering() {
        given(request.getPowerplayState()).willReturn(null);

        assertThat(underTest.matches(offerDetail, request)).isTrue();

        then(offerDetail).shouldHaveNoInteractions();
    }

    @Test
    void nullPowerplayState() {
        given(request.getPowerplayState()).willReturn(PowerplayState.FORTIFIED.name());
        given(offerDetail.getPowerplayState()).willReturn(null);

        assertThat(underTest.matches(offerDetail, request)).isFalse();
    }

    @Test
    void matchingPowerplayState() {
        given(request.getPowerplayState()).willReturn(PowerplayState.FORTIFIED.name());
        given(offerDetail.getPowerplayState()).willReturn(PowerplayState.FORTIFIED);

        assertThat(underTest.matches(offerDetail, request)).isTrue();
    }

    @Test
    void nonMatchingPowerplayState() {
        given(request.getPowerplayState()).willReturn(PowerplayState.FORTIFIED.name());
        given(offerDetail.getPowerplayState()).willReturn(PowerplayState.UNOCCUPIED);

        assertThat(underTest.matches(offerDetail, request)).isFalse();
    }
}