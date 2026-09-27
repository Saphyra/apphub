package com.github.saphyra.apphub.ci.service.integration_server;

import com.github.saphyra.apphub.ci.api.model.integration_server.ReportTestCaseRequest;
import com.github.saphyra.apphub.ci.dao.integration_server.test_case_run.TestCaseRun;
import com.github.saphyra.apphub.ci.dao.integration_server.test_case_run.TestCaseRunRepository;
import com.github.saphyra.apphub.ci.util.IdGenerator;
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
