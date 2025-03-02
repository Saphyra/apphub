package com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class EdConflict {
    @JsonProperty("Status")
    private String status;

    @JsonProperty("WarType")
    private String warType;

    @JsonProperty("Faction1")
    private ConflictFaction faction1;

    @JsonProperty("Faction2")
    private ConflictFaction faction2;
}
