package com.github.saphyra.integration.server.api.test_case;

import com.github.saphyra.integration.server.api.OneParam;
import com.github.saphyra.integration.server.api.test_case.request.ReportTestCaseRequest;
import com.github.saphyra.integration.server.api.test_case.request.TestCaseRunRequest;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TestCaseController {
    private static volatile int MAX_LENGTH = Integer.MIN_VALUE;

    private final TestCaseRunService testCaseRunService;
    private final TestCaseRunTimeQueryService testCaseRunTimeQueryService;

    @PutMapping(Endpoints.REPORT_TEST_CASE)
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

    @PostMapping(Endpoints.GET_AVERAGE_RUN_TIME)
    Long getAverageRunTime(@RequestBody OneParam<String> testCaseId) {
        log.info("Querying average run time of testCase {}", testCaseId);
        Long averageRunTime = testCaseRunTimeQueryService.getAverageRunTime(testCaseId.getValue());
        log.info("Average run time of testCase {} is: {}", testCaseId.getValue(), averageRunTime);
        return averageRunTime;
    }
}
