package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.CommodityTradingRequest;
import com.github.saphyra.apphub.service.custom.elite_base.dao.StationType;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.detail.OfferDetail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FleetCarrierOfferFilterTest {
    @InjectMocks
    private FleetCarrierOfferFilter underTest;

    @Mock
    private OfferDetail offerDetail;

    @Mock
    private CommodityTradingRequest request;

    @Test
    void includeFleetCarriers() {
        given(request.getIncludeFleetCarriers()).willReturn(true);

        assertThat(underTest.filter(List.of(offerDetail), request)).containsExactly(offerDetail);
    }

    @Test
    void filterFleetCarriers() {
        given(request.getIncludeFleetCarriers()).willReturn(false);
        given(offerDetail.getStationType()).willReturn(StationType.FLEET_CARRIER);

        assertThat(underTest.filter(List.of(offerDetail), request)).isEmpty();
    }

    @Test
    void allowStation() {
        given(request.getIncludeFleetCarriers()).willReturn(false);
        given(offerDetail.getStationType()).willReturn(StationType.ASTEROID_BASE);

        assertThat(underTest.filter(List.of(offerDetail), request)).containsExactly(offerDetail);
    }
}