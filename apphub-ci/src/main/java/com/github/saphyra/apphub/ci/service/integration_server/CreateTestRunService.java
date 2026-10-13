package com.github.saphyra.apphub.ci.service.integration_server;

import com.github.saphyra.apphub.ci.api.model.integration_server.TestRunStatus;
import com.github.saphyra.apphub.ci.dao.integration_server.test_run.TestRun;
import com.github.saphyra.apphub.ci.dao.integration_server.test_run.TestRunRepository;
import com.github.saphyra.apphub.ci.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateTestRunService {
    private final TestRunRepository testRunRepository;
    private final IdGenerator uuidGenerator;

    public UUID create() {
        UUID testRunId = uuidGenerator.randomUuid();

        TestRun testRun = TestRun.builder()
            .id(testRunId)
            .status(TestRunStatus.PENDING)
            .build();

        testRunRepository.save(testRun);

        return testRunId;
    }
}
