package com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.minor_faction.StarSystemMinorFactionMapping;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.minor_faction.StarSystemMinorFactionMappingDao;
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
class MinorFactionOrphanedRecordCleanerTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID MINOR_FACTION_ID = UUID.randomUUID();

    @Autowired
    private MinorFactionDao minorFactionDao;

    @Autowired
    private StarSystemMinorFactionMappingDao starSystemMinorFactionMappingDao;

    @Autowired
    private MinorFactionOrphanedRecordCleaner underTest;

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
        StarSystemMinorFactionMapping mapping = StarSystemMinorFactionMapping.builder()
            .starSystemId(STAR_SYSTEM_ID)
            .minorFactionId(MINOR_FACTION_ID)
            .build();
        starSystemMinorFactionMappingDao.save(mapping);
        MinorFaction minorFaction = MinorFaction.builder()
            .id(MINOR_FACTION_ID)
            .build();
        minorFactionDao.save(minorFaction);
        MinorFaction orphanedMinorFaction = MinorFaction.builder()
            .id(UUID.randomUUID())
            .build();
        minorFactionDao.save(orphanedMinorFaction);

        assertThat(underTest.cleanupOrphanedRecords()).isEqualTo(1);

        assertThat(minorFactionDao.findAll()).containsExactly(minorFaction);
    }
}