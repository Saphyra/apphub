package com.github.saphyra.apphub.integration.structure;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModulesResponse {
    private String name;
    private String url;
    private boolean favorite;
}
