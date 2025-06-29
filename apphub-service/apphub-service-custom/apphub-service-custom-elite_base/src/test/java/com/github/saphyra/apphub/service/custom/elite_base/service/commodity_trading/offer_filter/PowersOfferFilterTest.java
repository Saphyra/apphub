package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import com.github.saphyra.apphub.api.custom.elite_base.model.Relation;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.Power;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PowersOfferFilterTest {
    @InjectMocks
    private PowersOfferFilter underTest;

    @Mock
    private CommodityTradingRequest request;

    @Mock
    private CommodityTradingResponse response;

    @Test
    void matches(){
        given(request.getPowersRelation()).willReturn(Relation.ANY);
        given(response.getPowers()).willReturn(List.of(Power.NAKATO_KAINE.name()));

        assertThat(underTest.matches(response, request)).isTrue();
    }

    @Test
    void doesNotMatch(){
        given(request.getPowersRelation()).willReturn(Relation.EMPTY);
        given(response.getPowers()).willReturn(List.of(Power.NAKATO_KAINE.name()));

        assertThat(underTest.matches(response, request)).isFalse();
    }
}