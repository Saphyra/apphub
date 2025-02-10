package com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.navroute;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Waypoint {
    @JsonProperty("SystemAddress")
    private Long starId;

    @JsonProperty("StarSystem")
    private String starName;

    @JsonProperty("StarPos")
    private Double[] starPosition;

    @JsonProperty("StarClass")
    private String starType;
}
