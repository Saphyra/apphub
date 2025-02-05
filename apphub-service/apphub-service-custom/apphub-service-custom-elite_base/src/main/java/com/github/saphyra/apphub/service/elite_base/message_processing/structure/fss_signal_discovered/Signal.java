package com.github.saphyra.apphub.service.elite_base.message_processing.structure.fss_signal_discovered;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class Signal {
    @JsonProperty("SignalName")
    private String signalName;

    @JsonProperty("SignalType")
    private String signalType;

    @JsonProperty("SpawningPower")
    private String spawningPower;

    @JsonProperty("OpposingPower")
    private String opposingPower;

    @JsonProperty("SpawningFaction")
    private String spawningFaction;

    @JsonProperty("SpawningState")
    private String spawningState;

    @JsonProperty("ThreatLevel")
    private Integer threatLevel;

    @JsonProperty("USSType")
    private String ussType;

    private LocalDateTime timestamp;

    @JsonProperty("IsStation")
    private Boolean isStation;
}
