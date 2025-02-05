package com.github.saphyra.apphub.service.elite_base.message_processing.util;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.minor_faction.MinorFaction;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.conflict.MinorFactionConflict;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.conflict.WarStatus;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.conflict.WarType;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.conflict.minor_faction.ConflictingMinorFaction;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.conflict.minor_faction.ConflictingMinorFactionFactory;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.ConflictFaction;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.EdConflict;
import com.github.saphyra.apphub.test.common.CustomAssertions;
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
class ConflictMapperTest {
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID CONFLICT_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ConflictingMinorFactionFactory conflictingMinorFactionFactory;

    @InjectMocks
    private ConflictMapper underTest;

    @Mock
    private MinorFaction minorFaction;

    @Mock
    private ConflictFaction faction1;

    @Mock
    private ConflictFaction faction2;

    @Mock
    private ConflictingMinorFaction conflictingMinorFaction;

    @Test
    void nullConflicts() {
        assertThat(underTest.mapConflicts(LAST_UPDATE, STAR_SYSTEM_ID, null, List.of(minorFaction))).isNull();
    }

    @Test
    void mapConflict() {
        EdConflict conflict = EdConflict.builder()
            .status(WarStatus.ACTIVE.getValue())
            .warType(WarType.CIVIL_WAR.getValue())
            .faction1(faction1)
            .faction2(faction2)
            .build();

        given(idGenerator.randomUuid()).willReturn(CONFLICT_ID);
        given(conflictingMinorFactionFactory.create(LAST_UPDATE, CONFLICT_ID, faction1, List.of(minorFaction))).willReturn(conflictingMinorFaction);
        given(conflictingMinorFactionFactory.create(LAST_UPDATE, CONFLICT_ID, faction2, List.of(minorFaction))).willReturn(conflictingMinorFaction);

        CustomAssertions.singleListAssertThat(underTest.mapConflicts(LAST_UPDATE, STAR_SYSTEM_ID, new EdConflict[]{conflict}, List.of(minorFaction)))
            .returns(CONFLICT_ID, MinorFactionConflict::getId)
            .returns(STAR_SYSTEM_ID, MinorFactionConflict::getStarSystemId)
            .returns(WarStatus.ACTIVE, MinorFactionConflict::getStatus)
            .returns(WarType.CIVIL_WAR, MinorFactionConflict::getWarType)
            .returns(List.of(conflictingMinorFaction, conflictingMinorFaction), MinorFactionConflict::getConflictingMinorFactions);

    }
}