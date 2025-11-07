package com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.state;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.FactionStateEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.MinorFaction;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.MinorFactionDao;
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
class MinorFactionStateOrphanedRecordCleanerTest {
    private static final UUID MINOR_FACTION_ID = UUID.randomUUID();

    @Autowired
    private MinorFactionStateDao minorFactionStateDao;

    @Autowired
    private MinorFactionDao minorFactionDao;

    @Autowired
    private MinorFactionStateOrphanedRecordCleaner underTest;

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
        MinorFactionState minorFactionState = MinorFactionState.builder()
            .status(StateStatus.PENDING)
            .state(FactionStateEnum.BOOM)
            .minorFactionId(MINOR_FACTION_ID)
            .build();
        minorFactionStateDao.save(minorFactionState);
        MinorFactionState orphanedRecord = MinorFactionState.builder()
            .status(StateStatus.PENDING)
            .state(FactionStateEnum.BOOM)
            .minorFactionId(UUID.randomUUID())
            .build();
        minorFactionStateDao.save(orphanedRecord);

        assertThat(underTest.cleanupOrphanedRecords()).isEqualTo(1);

        assertThat(minorFactionStateDao.findAll()).containsExactly(minorFactionState);
    }
}