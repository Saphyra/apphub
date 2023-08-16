package com.github.saphyra.integration.server.api.test_case.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReportTestCaseRequest {
    private TestCaseRequest testCase;
    private TestCaseRunRequest testCaseRun;
}
