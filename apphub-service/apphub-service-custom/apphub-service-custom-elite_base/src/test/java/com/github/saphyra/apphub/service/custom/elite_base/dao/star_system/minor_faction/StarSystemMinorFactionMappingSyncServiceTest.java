package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.minor_faction;

import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.minor_faction.StarSystemMinorFactionMapping;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.minor_faction.StarSystemMinorFactionMappingDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.minor_faction.StarSystemMinorFactionMappingFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.minor_faction.StarSystemMinorFactionMappingSyncService;
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
class StarSystemMinorFactionMappingSyncServiceTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID EXISTING_MINOR_FACTION_ID = UUID.randomUUID();
    private static final UUID NEW_MINOR_FACTION_ID = UUID.randomUUID();

    @Mock
    private StarSystemMinorFactionMappingFactory starSystemMinorFactionMappingFactory;

    @Mock
    private StarSystemMinorFactionMappingDao starSystemMinorFactionMappingDao;

    @InjectMocks
    private StarSystemMinorFactionMappingSyncService underTest;

    @Mock
    private StarSystemMinorFactionMapping existingMapping;

    @Mock
    private StarSystemMinorFactionMapping newMapping;

    @Mock
    private StarSystemMinorFactionMapping deprecatedMapping;

    @Test
    void sync() {
        given(starSystemMinorFactionMappingFactory.create(STAR_SYSTEM_ID, EXISTING_MINOR_FACTION_ID)).willReturn(existingMapping);
        given(starSystemMinorFactionMappingFactory.create(STAR_SYSTEM_ID, NEW_MINOR_FACTION_ID)).willReturn(newMapping);

        given(starSystemMinorFactionMappingDao.getByStarSystemId(STAR_SYSTEM_ID)).willReturn(List.of(deprecatedMapping, existingMapping));

        underTest.sync(STAR_SYSTEM_ID, List.of(EXISTING_MINOR_FACTION_ID, NEW_MINOR_FACTION_ID));

        then(starSystemMinorFactionMappingDao).should().deleteAll(List.of(deprecatedMapping));
        then(starSystemMinorFactionMappingDao).should().saveAll(List.of(newMapping));
    }
}