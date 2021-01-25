package com.github.saphyra.apphub.api.utils.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LogParameterVisibilityResponse {
    private UUID id;
    private String parameter;
    private boolean visibility;
}
