package com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Ring {
    @JsonProperty("Name")
    private String ringName;

    @JsonProperty("RingClass")
    private String ringClass;

    @JsonProperty("InnerRad")
    private Double innerRadius;

    @JsonProperty("OuterRad")
    private Double outerRadius;

    @JsonProperty("MassMT")
    private Double mass;
}
