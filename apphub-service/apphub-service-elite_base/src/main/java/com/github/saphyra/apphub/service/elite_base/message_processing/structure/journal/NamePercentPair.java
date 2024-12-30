package com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class NamePercentPair {
    @JsonProperty("Name")
    private String name;

    @JsonProperty("Percent")
    private Double percent;
}
