package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.conflict.minor_faction;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.conflict.minor_faction.ConflictingMinorFaction;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.conflict.minor_faction.ConflictingMinorFactionConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.conflict.minor_faction.ConflictingMinorFactionEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ConflictingMinorFactionConverterTest {
    private static final UUID CONFLICT_ID = UUID.randomUUID();
    private static final UUID FACTION_ID = UUID.randomUUID();
    private static final Integer WON_DAYS = 34;
    private static final String STAKE = "stake";
    private static final String CONFLICT_ID_STRING = "conflict-id";
    private static final String FACTION_ID_STRING = "faction-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private ConflictingMinorFactionConverter underTest;

    @Test
    void convertDomain() {
        ConflictingMinorFaction domain = ConflictingMinorFaction.builder()
            .conflictId(CONFLICT_ID)
            .factionId(FACTION_ID)
            .wonDays(WON_DAYS)
            .stake(STAKE)
            .build();

        given(uuidConverter.convertDomain(CONFLICT_ID)).willReturn(CONFLICT_ID_STRING);
        given(uuidConverter.convertDomain(FACTION_ID)).willReturn(FACTION_ID_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(CONFLICT_ID_STRING, ConflictingMinorFactionEntity::getConflictId)
            .returns(FACTION_ID_STRING, ConflictingMinorFactionEntity::getFactionId)
            .returns(WON_DAYS, ConflictingMinorFactionEntity::getWonDays)
            .returns(STAKE, ConflictingMinorFactionEntity::getStake);
    }

    @Test
    void convertEntity() {
        ConflictingMinorFactionEntity domain = ConflictingMinorFactionEntity.builder()
            .conflictId(CONFLICT_ID_STRING)
            .factionId(FACTION_ID_STRING)
            .wonDays(WON_DAYS)
            .stake(STAKE)
            .build();

        given(uuidConverter.convertEntity(CONFLICT_ID_STRING)).willReturn(CONFLICT_ID);
        given(uuidConverter.convertEntity(FACTION_ID_STRING)).willReturn(FACTION_ID);

        assertThat(underTest.convertEntity(domain))
            .returns(CONFLICT_ID, ConflictingMinorFaction::getConflictId)
            .returns(FACTION_ID, ConflictingMinorFaction::getFactionId)
            .returns(WON_DAYS, ConflictingMinorFaction::getWonDays)
            .returns(STAKE, ConflictingMinorFaction::getStake);
    }
}