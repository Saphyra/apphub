package com.github.saphyra.apphub.ci.api.model.integration_server;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TestCaseRequest {
    private String id;
    private String name;
    private List<String> groups;
}
