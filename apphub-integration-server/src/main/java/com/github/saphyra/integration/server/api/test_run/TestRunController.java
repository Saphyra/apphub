package com.github.saphyra.integration.server.api.test_run;

import com.github.saphyra.integration.server.service.test_run.CreateTestRunService;
import com.github.saphyra.integration.server.util.Endpoints;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TestRunController {
    private final CreateTestRunService createTestRunService;

    @PutMapping(Endpoints.CREATE_TEST_RUN)
    UUID createTestRun() {
        log.info("Creating new TestRun...");
        UUID testRunId = createTestRunService.create();
        log.info("TestRun created with id {}", testRunId);
        return testRunId;
    }
}
