package com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class ControllingFaction {
    @JsonProperty("Name")
    private String factionName;

    @JsonProperty("FactionState")
    private String state; //Bust/None/Boom/Etc
}
