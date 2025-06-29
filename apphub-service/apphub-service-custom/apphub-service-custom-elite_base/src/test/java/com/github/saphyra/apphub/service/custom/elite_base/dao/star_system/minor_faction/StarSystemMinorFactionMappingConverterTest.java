package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.minor_faction;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.minor_faction.StarSystemMinorFactionMapping;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.minor_faction.StarSystemMinorFactionMappingConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.minor_faction.StarSystemMinorFactionMappingEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StarSystemMinorFactionMappingConverterTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID MINOR_FACTION_ID = UUID.randomUUID();
    private static final String STAR_SYSTEM_ID_STRING = "star-system-id";
    private static final String MINOR_FACTION_ID_STRING = "minor-faction-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private StarSystemMinorFactionMappingConverter underTest;

    @Test
    void convertDomain() {
        StarSystemMinorFactionMapping domain = StarSystemMinorFactionMapping.builder()
            .starSystemId(STAR_SYSTEM_ID)
            .minorFactionId(MINOR_FACTION_ID)
            .build();

        given(uuidConverter.convertDomain(STAR_SYSTEM_ID)).willReturn(STAR_SYSTEM_ID_STRING);
        given(uuidConverter.convertDomain(MINOR_FACTION_ID)).willReturn(MINOR_FACTION_ID_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(STAR_SYSTEM_ID_STRING, StarSystemMinorFactionMappingEntity::getStarSystemId)
            .returns(MINOR_FACTION_ID_STRING, StarSystemMinorFactionMappingEntity::getMinorFactionId);
    }

    @Test
    void convertEntity() {
        StarSystemMinorFactionMappingEntity domain = StarSystemMinorFactionMappingEntity.builder()
            .starSystemId(STAR_SYSTEM_ID_STRING)
            .minorFactionId(MINOR_FACTION_ID_STRING)
            .build();

        given(uuidConverter.convertEntity(STAR_SYSTEM_ID_STRING)).willReturn(STAR_SYSTEM_ID);
        given(uuidConverter.convertEntity(MINOR_FACTION_ID_STRING)).willReturn(MINOR_FACTION_ID);

        assertThat(underTest.convertEntity(domain))
            .returns(STAR_SYSTEM_ID, StarSystemMinorFactionMapping::getStarSystemId)
            .returns(MINOR_FACTION_ID, StarSystemMinorFactionMapping::getMinorFactionId);
    }
}