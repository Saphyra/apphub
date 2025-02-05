package com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.power;

import com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.Power;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class StarSystemPowerMappingSyncServiceTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    @Mock
    private StarSystemPowerMappingDao starSystemPowerMappingDao;

    @Mock
    private StarSystemPowerMappingFactory starSystemPowerMappingFactory;

    @InjectMocks
    private StarSystemPowerMappingSyncService underTest;

    @Mock
    private StarSystemPowerMapping existingMapping;

    @Mock
    private StarSystemPowerMapping newMapping;

    @Mock
    private StarSystemPowerMapping deprecatedMapping;

    @Test
    void sync_null() {
        underTest.sync(STAR_SYSTEM_ID, null);

        then(starSystemPowerMappingDao).shouldHaveNoInteractions();
        then(starSystemPowerMappingFactory).shouldHaveNoInteractions();
    }

    @Test
    void sync() {
        given(starSystemPowerMappingFactory.create(STAR_SYSTEM_ID, Power.NAKATO_KAINE)).willReturn(existingMapping);
        given(starSystemPowerMappingFactory.create(STAR_SYSTEM_ID, Power.ARCHON_DELAINE)).willReturn(newMapping);
        given(starSystemPowerMappingDao.getByStarSystemId(STAR_SYSTEM_ID)).willReturn(List.of(existingMapping, deprecatedMapping));

        underTest.sync(STAR_SYSTEM_ID, List.of(Power.NAKATO_KAINE, Power.ARCHON_DELAINE));

        then(starSystemPowerMappingDao).should().deleteAll(List.of(deprecatedMapping));
        then(starSystemPowerMappingDao).should().saveAll(List.of(newMapping));
    }
}