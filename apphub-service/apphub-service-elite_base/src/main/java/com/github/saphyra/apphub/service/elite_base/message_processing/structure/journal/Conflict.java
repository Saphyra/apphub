package com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Conflict {
    @JsonProperty("Status")
    private String status;

    @JsonProperty("WarType")
    private String warType;

    @JsonProperty("Faction1")
    private ConflictFaction faction1;

    @JsonProperty("Faction2")
    private ConflictFaction faction2;
}
