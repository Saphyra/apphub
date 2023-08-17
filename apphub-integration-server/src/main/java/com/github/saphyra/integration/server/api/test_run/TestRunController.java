package com.github.saphyra.integration.server.api.test_run;

import com.github.saphyra.integration.server.api.OneParam;
import com.github.saphyra.integration.server.service.test_run.CreateTestRunService;
import com.github.saphyra.integration.server.service.test_run.FinishTestRunService;
import com.github.saphyra.integration.server.util.Endpoints;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TestRunController {
    private final CreateTestRunService createTestRunService;
    private final FinishTestRunService finishTestRunService;

    @PutMapping(Endpoints.CREATE_TEST_RUN)
    UUID createTestRun() {
        log.info("Creating new TestRun...");
        UUID testRunId = createTestRunService.create();
        log.info("TestRun created with id {}", testRunId);
        return testRunId;
    }

    @PostMapping(Endpoints.FINISH_TEST_RUN)
    void finishTestRun(@PathVariable("testRunId") UUID testRunId, @RequestBody OneParam<TestRunStatus> status) {
        log.info("Finishing TestRun {} with status {}", testRunId, status);
        finishTestRunService.finishTestRun(testRunId, status.getValue());
        log.info("TestRun {} is finished.", testRunId);
    }
}
