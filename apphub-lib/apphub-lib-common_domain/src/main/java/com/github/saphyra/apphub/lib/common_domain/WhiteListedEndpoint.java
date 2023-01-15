package com.github.saphyra.apphub.lib.common_domain;

import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private String pattern;

    @NotNull
    private String method;

    public void validate() {
        Objects.requireNonNull(pattern, "pattern is null for " + this);
        Objects.requireNonNull(method, "method is null for " + this);
    }
}
