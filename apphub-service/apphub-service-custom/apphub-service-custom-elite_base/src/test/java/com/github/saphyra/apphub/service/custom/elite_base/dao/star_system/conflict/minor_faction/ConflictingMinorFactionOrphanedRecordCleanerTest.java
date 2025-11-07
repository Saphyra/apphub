package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.conflict.minor_faction;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.MinorFaction;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.MinorFactionDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.conflict.MinorFactionConflict;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.conflict.MinorFactionConflictDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ConflictingMinorFactionOrphanedRecordCleanerTest {
    private static final UUID MINOR_FACTION_ID = UUID.randomUUID();
    private static final UUID CONFLICT_ID = UUID.randomUUID();

    @Autowired
    private MinorFactionDao minorFactionDao;

    @Autowired
    private MinorFactionConflictDao minorFactionConflictDao;

    @Autowired
    private ConflictingMinorFactionDao conflictingMinorFactionDao;

    @Autowired
    private ConflictingMinorFactionOrphanedRecordCleaner underTest;

    @Autowired
    private List<CrudRepository<?, ?>> repositories;

    @MockBean
    private ErrorReporterService errorReporterService;

    @AfterEach
    void clear() {
        repositories.forEach(CrudRepository::deleteAll);
    }

    @Test
    void cleanup() {
        MinorFaction minorFaction = MinorFaction.builder()
            .id(MINOR_FACTION_ID)
            .build();
        minorFactionDao.save(minorFaction);
        ConflictingMinorFaction conflictingMinorFaction = ConflictingMinorFaction.builder()
            .conflictId(CONFLICT_ID)
            .minorFactionId(MINOR_FACTION_ID)
            .build();
        MinorFactionConflict minorFactionConflict = MinorFactionConflict.builder()
            .id(CONFLICT_ID)
            .conflictingMinorFactions(List.of(conflictingMinorFaction))
            .build();
        minorFactionConflictDao.save(minorFactionConflict);
        ConflictingMinorFaction onlyMinorFactionBoundConflict = ConflictingMinorFaction.builder()
            .conflictId(UUID.randomUUID())
            .minorFactionId(MINOR_FACTION_ID)
            .build();
        conflictingMinorFactionDao.save(onlyMinorFactionBoundConflict);
        ConflictingMinorFaction onlyConflictBoundConflict = ConflictingMinorFaction.builder()
            .conflictId(CONFLICT_ID)
            .minorFactionId(UUID.randomUUID())
            .build();
        conflictingMinorFactionDao.save(onlyConflictBoundConflict);

        assertThat(underTest.cleanupOrphanedRecords()).isEqualTo(2);

        assertThat(conflictingMinorFactionDao.findAll()).containsExactly(conflictingMinorFaction);
    }
}