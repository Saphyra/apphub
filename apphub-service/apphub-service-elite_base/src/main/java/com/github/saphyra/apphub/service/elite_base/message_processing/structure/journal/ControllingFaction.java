package com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ControllingFaction {
    @JsonProperty("Name")
    private String factionName;

    @JsonProperty("FactionState")
    private String economicState; //Bust/None/Boom/Etc
}
