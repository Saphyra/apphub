package com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.conflict.minor_faction;

import com.github.saphyra.apphub.service.elite_base.message_processing.dao.minor_faction.MinorFaction;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.ConflictFaction;
import com.github.saphyra.apphub.service.elite_base.message_processing.util.MinorFactionIdResolver;
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
class ConflictingMinorFactionFactoryTest {
    private static final String FACTION_NAME = "faction-name";
    private static final Integer WON_DAYS = 2435;
    private static final String STAKE = "stake";
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final UUID CONFLICT_ID = UUID.randomUUID();
    private static final UUID FACTION_ID = UUID.randomUUID();

    @Mock
    private MinorFactionIdResolver minorFactionIdResolver;

    @InjectMocks
    private ConflictingMinorFactionFactory underTest;

    @Mock
    private MinorFaction minorFaction;

    @Test
    void create() {
        ConflictFaction faction = ConflictFaction.builder()
            .factionName(FACTION_NAME)
            .wonDays(WON_DAYS)
            .stake(STAKE)
            .build();

        given(minorFactionIdResolver.getMinorFactionId(LAST_UPDATE, FACTION_NAME, List.of(minorFaction))).willReturn(FACTION_ID);

        assertThat(underTest.create(LAST_UPDATE, CONFLICT_ID, faction, List.of(minorFaction)))
            .returns(CONFLICT_ID, ConflictingMinorFaction::getConflictId)
            .returns(FACTION_ID, ConflictingMinorFaction::getFactionId)
            .returns(WON_DAYS, ConflictingMinorFaction::getWonDays)
            .returns(STAKE, ConflictingMinorFaction::getStake);
    }
}