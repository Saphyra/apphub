package com.github.saphyra.apphub.api.modules.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString(exclude = "favorite")
public class ModuleResponse {
    private String name;
    private String url;
    private boolean favorite;
}
