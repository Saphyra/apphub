package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.powerplay_conflict;

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
class PowerplayConflictSyncServiceTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();

    @Mock
    private PowerplayConflictDao powerplayConflictDao;

    @InjectMocks
    private PowerplayConflictSyncService underTest;

    @Mock
    private PowerplayConflict newConflict;

    @Mock
    private PowerplayConflict existingConflict;

    @Mock
    private PowerplayConflict removedConflict;

    @Test
    void sync() {
        given(powerplayConflictDao.getByStarSystemId(STAR_SYSTEM_ID)).willReturn(List.of(existingConflict, removedConflict));

        underTest.sync(STAR_SYSTEM_ID, List.of(newConflict, existingConflict));

        then(powerplayConflictDao).should().deleteAll(List.of(removedConflict));
        then(powerplayConflictDao).should().saveAll(List.of(newConflict));
    }
}