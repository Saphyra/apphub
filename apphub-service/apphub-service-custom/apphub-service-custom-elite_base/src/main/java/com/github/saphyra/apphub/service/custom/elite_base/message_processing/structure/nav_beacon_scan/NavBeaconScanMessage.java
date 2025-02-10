package com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.nav_beacon_scan;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class NavBeaconScanMessage {
    private String event;
    private LocalDateTime timestamp;

    @JsonProperty("SystemAddress")
    private Long starId;

    @JsonProperty("StarSystem")
    private String starName;

    @JsonProperty("StarPos")
    private Double[] starPosition;

    //Unused
    @JsonProperty("NumBodies")
    private Integer bodyCount;

    private Boolean horizons;
    private Boolean odyssey;
}
