package com.github.saphyra.integration.server.cleanup;

import com.github.saphyra.integration.server.domain.test_case.TestCase;
import com.github.saphyra.integration.server.domain.test_case.TestCaseRepository;
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
public class TestCaseCleanupService {
    private final TestCaseRepository testCaseRepository;
    private final DateTimeUtil dateTimeUtil;
    private final CleanupProperties cleanupProperties;

    @EventListener(ApplicationReadyEvent.class)
    void cleanExpiredTestRuns() {
        log.info("Cleaning expired test runs...");
        OffsetDateTime expirationTime = dateTimeUtil.getCurrentOffsetDateTime()
            .minusDays(cleanupProperties.getExpirationDays());

        List<TestCase> testCases = testCaseRepository.getByLastRunBefore(expirationTime);
        for (TestCase testCase : testCases) {
            log.info("Deleting testCase {}, it ran last time at {}", testCase.getName(), testCase.getLastRun());
            testCaseRepository.delete(testCase);
        }
        log.info("Expired TestCase cleanup finished. {} TestCases were deleted.", testCases.size());
    }
}
