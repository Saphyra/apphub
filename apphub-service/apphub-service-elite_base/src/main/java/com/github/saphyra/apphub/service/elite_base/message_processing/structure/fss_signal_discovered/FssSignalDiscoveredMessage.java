package com.github.saphyra.apphub.service.elite_base.message_processing.structure.fss_signal_discovered;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class FssSignalDiscoveredMessage {
    private String event;
    private LocalDateTime timestamp;

    @JsonProperty("SystemAddress")
    private Long starId;

    @JsonProperty("StarSystem")
    private String starName;

    @JsonProperty("StarPos")
    private Double[] starPosition;

    private Signal[] signals;

    //Unused
    private Boolean horizons;
    private Boolean odyssey;
}
