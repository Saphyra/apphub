package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.conflict.minor_faction;

import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.conflict.minor_faction.ConflictingMinorFaction;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.conflict.minor_faction.ConflictingMinorFactionDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.conflict.minor_faction.ConflictingMinorFactionSyncService;
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
class ConflictingMinorFactionSyncServiceTest {
    private static final UUID CONFLICT_ID = UUID.randomUUID();

    @Mock
    private ConflictingMinorFactionDao conflictingMinorFactionDao;

    @InjectMocks
    private ConflictingMinorFactionSyncService underTest;

    @Mock
    private ConflictingMinorFaction existingFaction;

    @Mock
    private ConflictingMinorFaction newFaction;

    @Mock
    private ConflictingMinorFaction deprecatedFaction;

    @Test
    void sync() {
        given(conflictingMinorFactionDao.getByConflictId(CONFLICT_ID)).willReturn(List.of(existingFaction, deprecatedFaction));

        underTest.sync(CONFLICT_ID, List.of(existingFaction, newFaction));

        then(conflictingMinorFactionDao).should().deleteAll(List.of(deprecatedFaction));
        then(conflictingMinorFactionDao).should().saveAll(List.of(newFaction));
    }
}