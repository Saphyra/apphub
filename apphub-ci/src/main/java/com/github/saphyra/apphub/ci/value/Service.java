package com.github.saphyra.apphub.ci.value;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Service {
    private String name;
    private Integer port;
    private String location;
}
