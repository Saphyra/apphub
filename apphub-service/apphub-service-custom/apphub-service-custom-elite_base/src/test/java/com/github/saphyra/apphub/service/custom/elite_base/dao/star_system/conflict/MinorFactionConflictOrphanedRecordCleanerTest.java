package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.conflict;

import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.StarSystemData;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.StarSystemDataDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class MinorFactionConflictOrphanedRecordCleanerTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID MINOR_FACTION_CONFLICT_ID_1 = UUID.randomUUID();

    @Autowired
    private StarSystemDataDao starSystemDataDao;

    @Autowired
    private MinorFactionConflictDao minorFactionConflictDao;

    @Autowired
    private MinorFactionConflictOrphanedRecordCleaner underTest;

    @Autowired
    private List<CrudRepository<?, ?>> repositories;

    @AfterEach
    void clear() {
        repositories.forEach(CrudRepository::deleteAll);
    }

    @Test
    void cleanup() {
        StarSystemData starSystemData = StarSystemData.builder()
            .starSystemId(STAR_SYSTEM_ID)
            .build();
        starSystemDataDao.save(starSystemData);
        MinorFactionConflict minorFactionConflict = MinorFactionConflict.builder()
            .id(MINOR_FACTION_CONFLICT_ID_1)
            .starSystemId(STAR_SYSTEM_ID)
            .build();
        minorFactionConflictDao.save(minorFactionConflict);
        MinorFactionConflict orphanedMinorFactionConflict = MinorFactionConflict.builder()
            .id(UUID.randomUUID())
            .starSystemId(UUID.randomUUID())
            .build();
        minorFactionConflictDao.save(orphanedMinorFactionConflict);

        underTest.cleanupOrphanedRecords();

        assertThat(minorFactionConflictDao.findAll()).containsExactly(minorFactionConflict);
    }
}