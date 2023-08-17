package com.github.saphyra.integration.server.service.test_run;

import com.github.saphyra.integration.server.api.test_run.TestRunStatus;
import com.github.saphyra.integration.server.domain.test_run.TestRun;
import com.github.saphyra.integration.server.domain.test_run.TestRunRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FinishTestRunService {
    private final TestRunRepository testRunRepository;

    public void finishTestRun(UUID testRunId, TestRunStatus status) {
        TestRun testRun = testRunRepository.findById(testRunId)
            .orElseThrow();

        testRun.setStatus(status);

        testRunRepository.save(testRun);
    }
}
