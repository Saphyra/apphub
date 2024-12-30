package com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.Economy;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.ControllingFaction;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DockedJournalMessage extends JournalMessage {
    @JsonProperty("BodyType")
    private String bodyType;

    @JsonProperty("StationType")
    private String stationType;

    @JsonProperty("StationName")
    private String stationName;

    @JsonProperty("DistFromStarLS")
    private Double distanceFromStar;

    @JsonProperty("LandingPads")
    private Map<String, Integer> landingPads;

    @JsonProperty("MarketID")
    private Long marketId;

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

    @JsonProperty("StationState")
    private String stationState;

    //Unused
    @JsonProperty("StationGovernment")
    private String government;

    @JsonProperty("Taxi")
    private Boolean taxi;

    @JsonProperty("Multicrew")
    private Boolean multicrew;
}
