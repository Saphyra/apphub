package com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.scan_bary_centre;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanBaryCentreMessage {
    private String event;
    private LocalDateTime timestamp;

    @JsonProperty("SystemAddress")
    private Long starId;

    @JsonProperty("StarSystem")
    private String starName;

    @JsonProperty("StarPos")
    private Double[] starPosition;

    @JsonProperty("BodyID")
    private Long bodyId;

    //Unused
    @JsonProperty("AscendingNode")
    private Double ascendingNode;

    @JsonProperty("Eccentricity")
    private Double eccentricity;

    @JsonProperty("MeanAnomaly")
    private Double meanAnomaly;

    @JsonProperty("OrbitalInclination")
    private Double orbitalInclination;

    @JsonProperty("OrbitalPeriod")
    private Double orbitalPeriod;

    @JsonProperty("Periapsis")
    private Double periapsis;

    @JsonProperty("SemiMajorAxis")
    private Double semiMajorAxis;

    private Boolean horizons;
    private Boolean odyssey;
}
