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
}
