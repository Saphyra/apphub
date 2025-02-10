package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.minor_faction;

import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.minor_faction.StarSystemMinorFactionMapping;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.minor_faction.StarSystemMinorFactionMappingFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class StarSystemMinorFactionMappingFactoryTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID MINOR_FACTION_ID = UUID.randomUUID();

    @InjectMocks
    private StarSystemMinorFactionMappingFactory underTest;

    @Test
    void create() {
        assertThat(underTest.create(STAR_SYSTEM_ID, MINOR_FACTION_ID))
            .returns(STAR_SYSTEM_ID, StarSystemMinorFactionMapping::getStarSystemId)
            .returns(MINOR_FACTION_ID, StarSystemMinorFactionMapping::getMinorFactionId);
    }
}