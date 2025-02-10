package com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.fss_all_bodies_found;

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
public class FssAllBodiesFoundMessage {
    private String event;
    private LocalDateTime timestamp;

    @JsonProperty("SystemAddress")
    private Long starId;

    @JsonProperty("SystemName")
    private String starName;

    @JsonProperty("StarPos")
    private Double[] starPosition;

    @JsonProperty("Count")
    private Integer bodyCount;

    private Boolean horizons;
    private Boolean odyssey;
}
