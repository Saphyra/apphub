package com.github.saphyra.apphub.ci.value;

import com.github.saphyra.apphub.ci.dao.PropertyName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
    @Builder.Default
    private List<PropertyName> properties = new ArrayList<>();
}
