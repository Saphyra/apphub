package com.github.saphyra.apphub.ci.api.model.integration_server;

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

    @Override
    public String toString() {
        return testCaseRun.toString();
    }
}
