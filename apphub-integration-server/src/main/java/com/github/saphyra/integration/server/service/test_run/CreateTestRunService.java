package com.github.saphyra.integration.server.service.test_run;

import com.github.saphyra.integration.server.api.test_run.TestRunStatus;
import com.github.saphyra.integration.server.domain.test_run.TestRun;
import com.github.saphyra.integration.server.domain.test_run.TestRunRepository;
import com.github.saphyra.integration.server.util.UuidGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateTestRunService {
    private final TestRunRepository testRunRepository;
    private final UuidGenerator uuidGenerator;

    public UUID create() {
        UUID testRunId = uuidGenerator.create();

        TestRun testRun = TestRun.builder()
            .id(testRunId)
            .status(TestRunStatus.PENDING)
            .build();

        testRunRepository.save(testRun);

        return testRunId;
    }
}
