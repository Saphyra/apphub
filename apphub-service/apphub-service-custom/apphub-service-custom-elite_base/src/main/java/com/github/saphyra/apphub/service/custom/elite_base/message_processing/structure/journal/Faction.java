package com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
