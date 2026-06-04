package com.github.saphyra.apphub.service.feature.calendar.domain.label.deprecated_dao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
@Deprecated(forRemoval = true)
public class DeprecatedLabel {
    private final UUID labelId;
    private final UUID userId;
    private String label;
}
