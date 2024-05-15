package com.github.saphyra.integration.server.service.test_case_run;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.integration.server.api.test_case.request.ReportTestCaseRequest;
import com.github.saphyra.integration.server.domain.test_case_run.TestCaseRun;
import com.github.saphyra.integration.server.domain.test_case_run.TestCaseRunRepository;
import com.github.saphyra.integration.server.service.test_case.TestCaseSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestCaseRunService {
    private final TestCaseSyncService testCaseSyncService;
    private final IdGenerator uuidGenerator;
    private final TestCaseRunRepository testCaseRunRepository;

    public void report(UUID testRunId, ReportTestCaseRequest request) {
        testCaseSyncService.createOrUpdate(request.getTestCase());

        UUID testCaseRunId = uuidGenerator.randomUuid();

        TestCaseRun testCaseRun = TestCaseRun.builder()
            .id(testCaseRunId)
            .testRunId(testRunId)
            .testCaseId(request.getTestCase().getId())
            .duration(request.getTestCaseRun().getDuration())
            .status(request.getTestCaseRun().getStatus())
            .build();

        testCaseRunRepository.save(testCaseRun);
    }
}
