package com.github.saphyra.apphub.api.modules.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ModuleResponse {
    private String name;
    private String url;
    private boolean favorite;
}
