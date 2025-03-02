package com.github.saphyra.apphub.service.custom.elite_base.message_processing.util;

import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.MinorFaction;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.MinorFactionSaver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MinorFactionIdResolverTest {
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final String FACTION_NAME = "faction-name";
    private static final UUID FACTION_ID = UUID.randomUUID();

    @Mock
    private MinorFactionSaver minorFactionSaver;

    @InjectMocks
    private MinorFactionIdResolver underTest;

    @Mock
    private MinorFaction minorFaction;

    @Test
    void nullMinorFactions(){
        given(minorFactionSaver.save(LAST_UPDATE, FACTION_NAME, null)).willReturn(minorFaction);
        given(minorFaction.getId()).willReturn(FACTION_ID);

        assertThat(underTest.getMinorFactionId(LAST_UPDATE, FACTION_NAME, null)).isEqualTo(FACTION_ID);
    }

    @Test
    void minorFactionFound(){
        given(minorFaction.getFactionName()).willReturn(FACTION_NAME);
        given(minorFaction.getId()).willReturn(FACTION_ID);

        assertThat(underTest.getMinorFactionId(LAST_UPDATE, FACTION_NAME, List.of(minorFaction))).isEqualTo(FACTION_ID);
    }
}