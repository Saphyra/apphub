package com.github.saphyra.integration.server.service.test_case_run;

import com.github.saphyra.integration.server.api.test_case.request.TestCaseRunStatus;
import com.github.saphyra.integration.server.domain.test_case_run.TestCaseRun;
import com.github.saphyra.integration.server.domain.test_case_run.TestCaseRunRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestCaseRunTimeQueryService {
    private final TestCaseRunRepository testCaseRunRepository;

    public Long getAverageRunTime(String testCaseId) {
        double averageTime = testCaseRunRepository.getByTestCaseId(testCaseId)
            .stream()
            .filter(testCaseRun -> testCaseRun.getStatus() == TestCaseRunStatus.PASSED)
            .mapToLong(TestCaseRun::getDuration)
            .average()
            .orElse(0);

        return Math.round(averageTime);
    }
}
