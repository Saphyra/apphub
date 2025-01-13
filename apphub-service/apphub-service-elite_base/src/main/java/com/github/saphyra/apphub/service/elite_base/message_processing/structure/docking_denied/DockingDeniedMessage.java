package com.github.saphyra.apphub.service.elite_base.message_processing.structure.docking_denied;

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
public class DockingDeniedMessage {
    private String event;
    private LocalDateTime timestamp;

    @JsonProperty("LandingPad")
    private Integer landingPad;

    @JsonProperty("MarketID")
    private Long marketId;

    @JsonProperty("StationType")
    private String stationType;

    @JsonProperty("StationName")
    private String stationName;

    @JsonProperty("Reason")
    private String reason;

    //Unused
    private Boolean horizons;
    private Boolean odyssey;
}
