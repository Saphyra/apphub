package com.github.saphyra.apphub.ci.api.model.integration_server;

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
