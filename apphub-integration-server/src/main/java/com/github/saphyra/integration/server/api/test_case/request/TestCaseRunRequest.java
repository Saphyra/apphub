package com.github.saphyra.integration.server.api.test_case.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TestCaseRunRequest {
    private String testCaseId;
    private Long duration;
    private TestCaseRunStatus status;

    @Override
    public String toString() {
        return String.format("TestCase %s %s in %s ms", testCaseId.replace("com.github.saphyra.apphub.integration.", ""), status, duration);
    }
}
