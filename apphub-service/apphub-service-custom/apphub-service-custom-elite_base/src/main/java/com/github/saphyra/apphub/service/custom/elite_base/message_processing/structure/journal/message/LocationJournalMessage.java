package com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.Economy;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.EdConflict;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.ControllingFaction;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.Faction;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.ThargoidWar;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class LocationJournalMessage extends JournalMessage {
    @JsonProperty("BodyType")
    private String bodyType;

    @JsonProperty("DistFromStarLS")
    private Double distanceFromStar;

    @JsonProperty("Factions")
    private Faction[] factions;

    @JsonProperty("Conflicts")
    private EdConflict[] conflicts;

    @JsonProperty("Population")
    private Long population;

    @JsonProperty("ControllingPower")
    private String controllingPower;

    @JsonProperty("PowerplayState")
    private String powerplayState;

    @JsonProperty("Powers")
    private String[] powers;

    @JsonProperty("SystemAllegiance")
    private String allegiance;

    @JsonProperty("SystemEconomy")
    private String economy;

    @JsonProperty("SystemSecondEconomy")
    private String secondEconomy;

    @JsonProperty("SystemFaction")
    private Object controllingFaction;

    @JsonProperty("SystemSecurity")
    private String securityLevel;

    @JsonProperty("MarketID")
    private Long marketId;

    @JsonProperty("StationAllegiance")
    private String stationAllegiance;

    @JsonProperty("StationEconomies")
    private Economy[] stationEconomies;

    @JsonProperty("StationEconomy")
    private String stationEconomy;

    @JsonProperty("StationName")
    private String stationName;

    @JsonProperty("StationType")
    private String stationType;

    @JsonProperty("StationServices")
    private String[] stationServices;

    @JsonProperty("StationFaction")
    private ControllingFaction stationFaction;

    //Unused
    @JsonProperty("Docked")
    private Boolean docked;

    @JsonProperty("OnFoot")
    private Boolean onFoot;

    @JsonProperty("SystemGovernment")
    private String government;

    @JsonProperty("StationGovernment")
    private String stationGovernment;

    @JsonProperty("Multicrew")
    private Boolean multicrew;

    @JsonProperty("InSRV")
    private Boolean inSrv;

    @JsonProperty("Taxi")
    private Boolean taxi;

    @JsonProperty("ThargoidWar")
    private ThargoidWar thargoidWar;

    @JsonProperty("FactionState")
    private String factionState;
}
