package com.github.saphyra.apphub.service.elite_base.message_processing.structure.approach_settlement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.Economy;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.ControllingFaction;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class ApproachSettlementMessage {
    private LocalDateTime timestamp;

    //Star
    @JsonProperty("SystemAddress")
    private Long starId;
    @JsonProperty("StarSystem")
    private String starName;
    @JsonProperty("StarPos")
    private Double[] starPosition;

    //Body
    @JsonProperty("BodyID")
    private Long bodyId;
    @JsonProperty("BodyName")
    private String bodyName;

    //Station
    @JsonProperty("MarketID")
    private Long marketId;
    @JsonProperty("Name")
    private String settlementName;
    @JsonProperty("StationEconomies")
    private Economy[] economies;
    @JsonProperty("StationEconomy")
    private String economy;
    @JsonProperty("StationFaction")
    private ControllingFaction controllingFaction;
    @JsonProperty("StationServices")
    private String[] stationServices;
    @JsonProperty("StationAllegiance")
    private String allegiance;

    //Location
    @JsonProperty("Latitude")
    private Double latitude;
    @JsonProperty("Longitude")
    private Double longitude;

    //Unused
    private String event;

    @JsonProperty("StationGovernment")
    private String government;

    private Boolean horizons;
    private Boolean odyssey;
}
