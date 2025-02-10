package com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.navroute;

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
public class NavrouteMessage {
    private String event;
    private LocalDateTime timestamp;

    @JsonProperty("Route")
    private Waypoint[] route;

    //Unused
    private Boolean horizons;
    private Boolean odyssey;
}
