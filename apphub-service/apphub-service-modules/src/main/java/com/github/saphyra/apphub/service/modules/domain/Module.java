package com.github.saphyra.apphub.service.modules.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class Module {
    @NotNull
    private String name;

    @NotNull
    private String url;

    private boolean mobileAllowed;

    @Builder.Default
    private List<String> roles = new ArrayList<>();
}
