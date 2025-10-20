package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.powerplay_conflict;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.common.BufferSynchronizationService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystemDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.Power;
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
class PowerplayConflictOrphanedRecordCleanerTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    @Autowired
    private StarSystemDao starSystemDao;

    @Autowired
    private PowerplayConflictDao powerplayConflictDao;

    @Autowired
    private PowerplayConflictOrphanedRecordCleaner underTest;

    @Autowired
    private BufferSynchronizationService bufferSynchronizationService;

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
        StarSystem starSystem = StarSystem.builder()
            .id(STAR_SYSTEM_ID)
            .build();
        starSystemDao.save(starSystem);
        PowerplayConflict powerplayConflict = PowerplayConflict.builder()
            .starSystemId(STAR_SYSTEM_ID)
            .power(Power.AISLING_DUVAL)
            .build();
        powerplayConflictDao.save(powerplayConflict);
        PowerplayConflict orphanedRecord = PowerplayConflict.builder()
            .starSystemId(UUID.randomUUID())
            .power(Power.NAKATO_KAINE)
            .build();
        powerplayConflictDao.save(orphanedRecord);
        bufferSynchronizationService.synchronizeAll();

        assertThat(underTest.cleanupOrphanedRecords()).isEqualTo(1);

        assertThat(powerplayConflictDao.findAll()).containsExactly(powerplayConflict);
    }
}