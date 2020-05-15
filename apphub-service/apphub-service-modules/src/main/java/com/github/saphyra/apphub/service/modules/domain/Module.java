package com.github.saphyra.apphub.service.modules.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class Module {
    @NotNull
    private String name;

    @NotNull
    private String url;

    private boolean allowedByDefault;

    private String role;
}
