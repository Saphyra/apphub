package com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.fss_discovery_scan;

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
public class FssDiscoveryScanMessage {
    private String event;
    private LocalDateTime timestamp;

    @JsonProperty("SystemAddress")
    private Long starId;

    @JsonProperty("SystemName")
    private String starName;

    @JsonProperty("StarPos")
    private Double[] starPosition;

    @JsonProperty("BodyCount")
    private Integer bodyCount;

    @JsonProperty("NonBodyCount")
    private Integer nonBodyCount;

    //Unused
    private Boolean horizons;
    private Boolean odyssey;
}
