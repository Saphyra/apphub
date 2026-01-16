package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.Relation;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.power.StarSystemPowerMapping;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.power.StarSystemPowerMappingDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.Power;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.OfferDetail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PowersOfferFilterTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();

    @Mock
    private StarSystemPowerMappingDao starSystemPowerMappingDao;

    @InjectMocks
    private PowersOfferFilter underTest;

    @Mock
    private CommodityTradingRequest request;

    @Mock
    private OfferDetail offerDetail;

    @Mock
    private StarSystem starSystem;

    @Mock
    private StarSystemPowerMapping starSystemPowerMapping;

    @Test
    void filter() {
        given(request.getPowersRelation()).willReturn(Relation.ANY_MATCH);
        given(request.getPowers()).willReturn(List.of(Power.NAKATO_KAINE.name()));
        given(offerDetail.getStarSystem()).willReturn(starSystem);
        given(starSystem.getId()).willReturn(STAR_SYSTEM_ID);
        given(starSystemPowerMappingDao.getByStarSystemIds(List.of(STAR_SYSTEM_ID))).willReturn(List.of(starSystemPowerMapping));
        given(starSystemPowerMapping.getStarSystemId()).willReturn(STAR_SYSTEM_ID);
        given(starSystemPowerMapping.getPower()).willReturn(Power.NAKATO_KAINE);

        assertThat(underTest.filter(List.of(offerDetail), request)).containsExactly(offerDetail);
    }

    @Test
    void doesNotMatch() {
        given(request.getPowersRelation()).willReturn(Relation.EMPTY);
        given(offerDetail.getStarSystem()).willReturn(starSystem);
        given(starSystem.getId()).willReturn(STAR_SYSTEM_ID);
        given(starSystemPowerMappingDao.getByStarSystemIds(List.of(STAR_SYSTEM_ID))).willReturn(List.of(starSystemPowerMapping));
        given(starSystemPowerMapping.getStarSystemId()).willReturn(STAR_SYSTEM_ID);
        given(starSystemPowerMapping.getPower()).willReturn(Power.NAKATO_KAINE);

        assertThat(underTest.filter(List.of(offerDetail), request)).isEmpty();
    }
}