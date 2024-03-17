package com.github.saphyra.integration.server.cleanup;

import com.github.saphyra.integration.server.domain.test_case_run.TestCaseRun;
import com.github.saphyra.integration.server.domain.test_case_run.TestCaseRunRepository;
import com.github.saphyra.integration.server.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestCaseRunCleanupService {
    private final TestCaseRunRepository testCaseRunRepository;
    private final DateTimeUtil dateTimeUtil;
    private final CleanupProperties cleanupProperties;

    @EventListener(ApplicationReadyEvent.class)
    void cleanExpiredTestRuns() {
        log.info("Cleaning expired test runs...");
        OffsetDateTime expirationTime = dateTimeUtil.getCurrentOffsetDateTime()
            .minusDays(cleanupProperties.getExpirationDays());

        List<TestCaseRun> testCaseRuns = testCaseRunRepository.getByCreatedAtBefore(expirationTime);
        for (TestCaseRun testCaseRun : testCaseRuns) {
            log.info("Deleting testCaseRun {}, it finished at {}", testCaseRun.getId(), testCaseRun.getCreatedAt());
            testCaseRunRepository.delete(testCaseRun);
        }
        log.info("Expired TestCaseRun cleanup finished. {} TestCaseRuns were deleted.", testCaseRuns.size());
    }
}
