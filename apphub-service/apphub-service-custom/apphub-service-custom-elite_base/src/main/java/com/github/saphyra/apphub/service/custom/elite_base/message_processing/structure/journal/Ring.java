package com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Ring {
    @JsonProperty("Name")
    private String ringName;

    @JsonProperty("RingClass")
    private String ringType;

    @JsonProperty("InnerRad")
    private Double innerRadius;

    @JsonProperty("OuterRad")
    private Double outerRadius;

    @JsonProperty("MassMT")
    private Double mass;
}
