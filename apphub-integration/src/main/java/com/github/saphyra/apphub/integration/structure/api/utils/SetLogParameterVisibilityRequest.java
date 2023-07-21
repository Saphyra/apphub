package com.github.saphyra.apphub.integration.structure.api.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SetLogParameterVisibilityRequest {
    private UUID id;
    private boolean visible;
}
