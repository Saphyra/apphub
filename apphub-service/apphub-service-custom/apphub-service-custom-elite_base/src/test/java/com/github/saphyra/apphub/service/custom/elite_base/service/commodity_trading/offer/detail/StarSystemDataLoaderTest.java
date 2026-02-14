package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.detail;

import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.power.StarSystemPowerMapping;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.power.StarSystemPowerMappingDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.Power;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.StarSystemData;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.StarSystemDataDao;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.Offer;
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
class StarSystemDataLoaderTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();

    @Mock
    private StarSystemPowerMappingDao starSystemPowerMappingDao;

    @Mock
    private StarSystemDataDao starSystemDataDao;

    @InjectMocks
    private StarSystemDataLoader underTest;

    @Mock
    private Offer offer;

    @Mock
    private StarSystemPowerMapping starSystemPowerMapping;

    @Mock
    private StarSystemData starSystemData;

    @Test
    void loadPowers() {
        given(offer.getStarSystemId()).willReturn(STAR_SYSTEM_ID);
        given(starSystemPowerMappingDao.getByStarSystemIds(List.of(STAR_SYSTEM_ID))).willReturn(List.of(starSystemPowerMapping));
        given(starSystemPowerMapping.getStarSystemId()).willReturn(STAR_SYSTEM_ID);
        given(starSystemPowerMapping.getPower()).willReturn(Power.ARCHON_DELAINE);

        assertThat(underTest.loadPowers(List.of(offer))).containsEntry(STAR_SYSTEM_ID, List.of(Power.ARCHON_DELAINE));
    }

    @Test
    void loadStarSystemData() {
        given(offer.getStarSystemId()).willReturn(STAR_SYSTEM_ID);
        given(starSystemDataDao.findAllById(List.of(STAR_SYSTEM_ID))).willReturn(List.of(starSystemData));
        given(starSystemData.getStarSystemId()).willReturn(STAR_SYSTEM_ID);

        assertThat(underTest.loadStarSystemData(List.of(offer))).containsEntry(STAR_SYSTEM_ID, starSystemData);
    }
}