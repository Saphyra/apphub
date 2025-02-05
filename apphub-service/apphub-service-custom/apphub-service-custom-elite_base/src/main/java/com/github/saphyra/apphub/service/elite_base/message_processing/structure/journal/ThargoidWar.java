package com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ThargoidWar {
    @JsonProperty("CurrentState")
    private String currentState;

    @JsonProperty("NextStateFailure")
    private String nextStateFailure;

    @JsonProperty("NextStateSuccess")
    private String nextStateSuccess;

    @JsonProperty("RemainingPorts")
    private Integer remainingPorts;

    @JsonProperty("SuccessStateReached")
    private Boolean successStateReached;

    @JsonProperty("WarProgress")
    private Double warProgress;

    @JsonProperty("EstimatedRemainingTime")
    private String estimatedRemainingTime;
}
