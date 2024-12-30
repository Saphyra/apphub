package com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Faction {
    @JsonProperty("Name")
    private String name;

    @JsonProperty("Influence")
    private Double influence;

    @JsonProperty("FactionState")
    private String state;

    @JsonProperty("Allegiance")
    private String allegiance;

    @JsonProperty("ActiveStates")
    private FactionState[] activeStates;

    @JsonProperty("PendingStates")
    private FactionState[] pendingStates;

    @JsonProperty("RecoveringStates")
    private FactionState[] recoveringStates;

    //Unused
    @JsonProperty("Happiness")
    private String happiness;

    @JsonProperty("Government")
    private String government;
}
