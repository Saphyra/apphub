package com.github.saphyra.integration.server.api.test_case;

import com.github.saphyra.integration.server.api.OneParam;
import com.github.saphyra.integration.server.api.test_case.request.ReportTestCaseRequest;
import com.github.saphyra.integration.server.service.test_case_run.TestCaseRunService;
import com.github.saphyra.integration.server.service.test_case_run.TestCaseRunTimeQueryService;
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
public class TestCaseController {
    private final TestCaseRunService testCaseRunService;
    private final TestCaseRunTimeQueryService testCaseRunTimeQueryService;

    @PutMapping(Endpoints.REPORT_TEST_CASE)
    void reportTestCase(@PathVariable("testRunId") UUID testRunId, @RequestBody ReportTestCaseRequest request) {
        log.info("{}", request);
        testCaseRunService.report(testRunId, request);
    }

    @PostMapping(Endpoints.GET_AVERAGE_RUN_TIME)
    Long getAverageRunTime(@RequestBody OneParam<String> testCaseId) {
        log.info("Querying average run time of testCase {}", testCaseId);
        Long averageRunTime = testCaseRunTimeQueryService.getAverageRunTime(testCaseId.getValue());
        log.info("Average run time of testCase {} is: {}", testCaseId.getValue(), averageRunTime);
        return averageRunTime;
    }
}
