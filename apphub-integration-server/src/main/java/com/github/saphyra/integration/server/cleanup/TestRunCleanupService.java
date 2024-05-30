package com.github.saphyra.integration.server.cleanup;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.integration.server.domain.test_run.TestRun;
import com.github.saphyra.integration.server.domain.test_run.TestRunRepository;
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
public class TestRunCleanupService {
    private final TestRunRepository testCaseRepository;
    private final DateTimeUtil dateTimeUtil;
    private final CleanupProperties cleanupProperties;

    @EventListener(ApplicationReadyEvent.class)
    void cleanExpiredTestRuns() {
        log.info("Cleaning expired test runs...");
        OffsetDateTime expirationTime = dateTimeUtil.getCurrentOffsetDateTime()
            .minusDays(cleanupProperties.getExpirationDays());

        List<TestRun> testRuns = testCaseRepository.getByEndTimeBefore(expirationTime);
        for (TestRun testRun : testRuns) {
            log.info("Deleting testRun {}, it finished at {}", testRun.getId(), testRun.getEndTime());
            testCaseRepository.delete(testRun);
        }
        log.info("Expired TestRun cleanup is finished. {} TestRuns were deleted.", testRuns.size());
    }
}
