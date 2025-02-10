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
public class ConflictFaction {
    @JsonProperty("Name")
    private String factionName;

    @JsonProperty("WonDays")
    private Integer wonDays;

    @JsonProperty("Stake")
    private String stake;
}
