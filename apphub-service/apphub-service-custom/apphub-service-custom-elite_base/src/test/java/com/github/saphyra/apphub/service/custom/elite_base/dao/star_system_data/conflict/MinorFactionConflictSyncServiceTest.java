package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.conflict;

import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.conflict.MinorFactionConflict;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.conflict.MinorFactionConflictDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.conflict.MinorFactionConflictSyncService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.conflict.minor_faction.ConflictingMinorFactionDao;
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
class MinorFactionConflictSyncServiceTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID EXISTING_CONFLICT_ID = UUID.randomUUID();
    private static final UUID NEW_CONFLICT_ID = UUID.randomUUID();
    private static final UUID DEPRECATED_CONFLICT_ID = UUID.randomUUID();

    @Mock
    private MinorFactionConflictDao minorFactionConflictDao;

    @Mock
    private ConflictingMinorFactionDao conflictingMinorFactionDao;

    @InjectMocks
    private MinorFactionConflictSyncService underTest;

    @Mock
    private MinorFactionConflict existingConflict;

    @Mock
    private MinorFactionConflict newConflict;

    @Mock
    private MinorFactionConflict deprecatedConflict;

    @Test
    void sync_null() {
        underTest.sync(STAR_SYSTEM_ID, null);

        then(minorFactionConflictDao).shouldHaveNoInteractions();
        then(conflictingMinorFactionDao).shouldHaveNoInteractions();
    }

    @Test
    void sync() {
        given(minorFactionConflictDao.getByStarSystemId(STAR_SYSTEM_ID)).willReturn(List.of(existingConflict, deprecatedConflict));
        given(existingConflict.getId()).willReturn(EXISTING_CONFLICT_ID);
        given(newConflict.getId()).willReturn(NEW_CONFLICT_ID);
        given(deprecatedConflict.getId()).willReturn(DEPRECATED_CONFLICT_ID);

        underTest.sync(STAR_SYSTEM_ID, List.of(existingConflict, newConflict));

        then(conflictingMinorFactionDao).should().deleteByConflictId(DEPRECATED_CONFLICT_ID);
        then(minorFactionConflictDao).should().deleteById(DEPRECATED_CONFLICT_ID);
        then(minorFactionConflictDao).should().saveAll(List.of(existingConflict, newConflict));
    }
}