package com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PowerplayConflictProgress {
    @JsonProperty("Power")
    private String power;

    @JsonProperty("ConflictProgress")
    private Double conflictProgress;
}
