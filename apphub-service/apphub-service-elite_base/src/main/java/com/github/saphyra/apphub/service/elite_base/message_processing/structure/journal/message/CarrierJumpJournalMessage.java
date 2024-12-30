package com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.Economy;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.Conflict;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.ControllingFaction;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.Faction;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.ThargoidWar;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CarrierJumpJournalMessage extends JournalMessage {
    @JsonProperty("BodyType")
    private String bodyType;

    @JsonProperty("Population")
    private Long population;

    @JsonProperty("SystemAllegiance")
    private String allegiance;

    @JsonProperty("SystemEconomy")
    private String economy;

    @JsonProperty("SystemSecondEconomy")
    private String secondEconomy;

    @JsonProperty("SystemSecurity")
    private String systemSecurity;

    @JsonProperty("MarketID")
    private Long marketId;

    @JsonProperty("StationName")
    private String carrierName;

    @JsonProperty("StationServices")
    private String[] services;

    @JsonProperty("ControllingPower")
    private String controllingPower;

    @JsonProperty("PowerplayState")
    private String powerplayState;

    @JsonProperty("Powers")
    private String[] powers;

    @JsonProperty("Factions")
    private Faction[] factions;

    @JsonProperty("SystemFaction")
    private ControllingFaction controllingFaction;

    @JsonProperty("Conflicts")
    private Conflict[] conflicts;

    //Unused
    @JsonProperty("Docked")
    private Boolean docked;

    @JsonProperty("OnFoot")
    private Boolean onFoot;

    @JsonProperty("SystemGovernment")
    private String government;

    @JsonProperty("StationGovernment")
    private String stationGovernment;

    @JsonProperty("ThargoidWar")
    private ThargoidWar thargoidWar;

    @JsonProperty("StationEconomies")
    private Economy[] economies;

    @JsonProperty("StationEconomy")
    private String stationEconomy;

    @JsonProperty("StationFaction")
    private ControllingFaction stationFaction;

    @JsonProperty("StationType")
    private String stationType;

    @JsonProperty("Multicrew")
    private Boolean multicrew;

    @JsonProperty("Taxi")
    private Boolean taxi;
}
