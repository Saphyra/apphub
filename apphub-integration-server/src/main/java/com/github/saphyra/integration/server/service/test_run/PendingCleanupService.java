package com.github.saphyra.integration.server.service.test_run;

import com.github.saphyra.integration.server.api.test_run.TestRunStatus;
import com.github.saphyra.integration.server.domain.test_run.TestRunRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class PendingCleanupService {
    private final TestRunRepository testRunRepository;

    @PostConstruct
    void cleanupPendingTestRuns() {
        log.info("Cleaning up pending TestRuns...");
        testRunRepository.getByStatus(TestRunStatus.PENDING)
            .forEach(testRun -> {
                testRun.setStatus(TestRunStatus.TIMEOUT);
                testRunRepository.save(testRun);
            });
    }
}
