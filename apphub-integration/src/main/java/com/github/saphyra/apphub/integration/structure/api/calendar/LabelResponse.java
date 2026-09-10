package com.github.saphyra.apphub.integration.structure.api.calendar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LabelResponse {
    private UUID labelId;
    private UUID userId;
    private String label;
    private Boolean shared;
}
