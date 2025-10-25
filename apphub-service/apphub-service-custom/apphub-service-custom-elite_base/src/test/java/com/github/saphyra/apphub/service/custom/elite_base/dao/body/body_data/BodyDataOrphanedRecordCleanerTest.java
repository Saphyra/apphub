package com.github.saphyra.apphub.service.custom.elite_base.dao.body.body_data;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.Body;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.BodyDao;
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
class BodyDataOrphanedRecordCleanerTest {
    private static final UUID BODY_ID = UUID.randomUUID();

    @Autowired
    private BodyDao bodyDao;

    @Autowired
    private BodyDataDao bodyDataDao;

    @Autowired
    private BodyDataOrphanedRecordCleaner underTest;

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
        Body body = Body.builder()
            .id(BODY_ID)
            .build();
        bodyDao.save(body);
        BodyData bodyData = BodyData.builder()
            .bodyId(BODY_ID)
            .build();
        bodyDataDao.save(bodyData);
        BodyData orphanedRecord = BodyData.builder()
            .bodyId(UUID.randomUUID())
            .build();
        bodyDataDao.save(orphanedRecord);

        assertThat(underTest.cleanupOrphanedRecords()).isEqualTo(1);

        assertThat(bodyDataDao.findAll()).containsExactly(bodyData);
    }
}