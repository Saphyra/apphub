package com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.fss_body_signals;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.Signal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class FssBodySignalsMessage {
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

    @JsonProperty("BodyName")
    private String bodyName;

    @JsonProperty("Signals")
    private Signal[] signals;

    private Boolean horizons;
    private Boolean odyssey;
}
