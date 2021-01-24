package com.github.saphyra.apphub.service.utils.log_formatter.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class LogParameterVisibility {
    private final UUID id;
    private final UUID userId;
    private final String parameter;
    private boolean visible;
}
