package com.github.saphyra.apphub.lib.common_domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhiteListedEndpoint {
    private String path;
    private String method;

    public void validate() {
        Objects.requireNonNull(path, "path is null for " + this);
        Objects.requireNonNull(method, "method is null for " + this);
    }
}
