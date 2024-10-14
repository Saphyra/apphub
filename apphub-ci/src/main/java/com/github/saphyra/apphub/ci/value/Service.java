package com.github.saphyra.apphub.ci.value;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class Service {
    private String name;
    private Integer port;
    private String location;
    private String moduleName;
    private Integer group;
    @Builder.Default
    private Boolean optional = false;
    private Integer healthCheckPort;
}
