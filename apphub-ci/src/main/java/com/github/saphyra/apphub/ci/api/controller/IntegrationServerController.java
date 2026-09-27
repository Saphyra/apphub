package com.github.saphyra.apphub.ci.api.controller;

import com.github.saphyra.apphub.ci.api.ApiConstants;
import com.github.saphyra.apphub.ci.api.model.OneParam;
import com.github.saphyra.apphub.ci.api.model.integration_server.ReportTestCaseRequest;
import com.github.saphyra.apphub.ci.api.model.integration_server.TestCaseRunRequest;
import com.github.saphyra.apphub.ci.api.model.integration_server.TestRunStatus;
import com.github.saphyra.apphub.ci.service.integration_server.CreateTestRunService;
import com.github.saphyra.apphub.ci.service.integration_server.FinishTestRunService;
import com.github.saphyra.apphub.ci.service.integration_server.TestCaseRunService;
import com.github.saphyra.apphub.ci.service.integration_server.TestCaseRunTimeQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
@Slf4j
class IntegrationServerController {
    private static volatile int MAX_LENGTH = Integer.MIN_VALUE;

    private final CreateTestRunService createTestRunService;
    private final FinishTestRunService finishTestRunService;
    private final TestCaseRunService testCaseRunService;
    private final TestCaseRunTimeQueryService testCaseRunTimeQueryService;

    @PutMapping(ApiConstants.PATH_CREATE_TEST_RUN)
    UUID createTestRun() {
        log.info("Creating new TestRun...");
        UUID testRunId = createTestRunService.create();
        log.info("TestRun created with id {}", testRunId);
        return testRunId;
    }

    @PostMapping(ApiConstants.PATH_FINISH_TEST_RUN)
    void finishTestRun(@PathVariable("testRunId") UUID testRunId, @RequestBody OneParam<TestRunStatus> status) {
        log.info("Finishing TestRun {} with status {}", testRunId, status.getValue());
        finishTestRunService.finishTestRun(testRunId, status.getValue());
        log.debug("TestRun {} is finished.", testRunId);
    }

    @PostMapping(ApiConstants.PATH_GET_AVERAGE_RUN_TIME)
    Long getAverageRunTime(@RequestBody OneParam<String> testCaseId) {
        Long averageRunTime = testCaseRunTimeQueryService.getAverageRunTime(testCaseId.getValue());
        log.info("Average run time of testCase {} is: {}", testCaseId.getValue(), averageRunTime);
        return averageRunTime;
    }

    @PutMapping(ApiConstants.PATH_REPORT_TEST_CASE)
    void reportTestCase(@PathVariable("testRunId") UUID testRunId, @RequestBody ReportTestCaseRequest request) {
        logRequest(request.getTestCaseRun());
        testCaseRunService.report(testRunId, request);
    }

    private synchronized void logRequest(TestCaseRunRequest request) {
        String displayedName = parse(request.getTestCaseId());

        if (displayedName.length() > MAX_LENGTH) {
            MAX_LENGTH = displayedName.length();
        }

        String suffix = Stream.generate(() -> " ")
            .limit(Math.max(0, MAX_LENGTH - displayedName.length()))
            .collect(Collectors.joining());

        displayedName += suffix;

        log.info("TestCase {} {} in {}ms", displayedName, request.getStatus(), request.getDuration());
    }

    private String parse(String testCaseId) {
        String[] name = testCaseId.replace("com.github.saphyra.apphub.integration.", "")
            .split("\\.");

        String type;
        switch (name[0]) {
            case "frontend" -> type = "[FE]";
            case "backend" -> type = "[BE]";
            default -> {
                log.error("TestCase is neither a BE or FE test. Value: {}", name[0]);
                type = testCaseId;
            }
        }

        return String.format("%s - %s:%s", type, name[name.length - 2], name[name.length - 1]);
    }
}
