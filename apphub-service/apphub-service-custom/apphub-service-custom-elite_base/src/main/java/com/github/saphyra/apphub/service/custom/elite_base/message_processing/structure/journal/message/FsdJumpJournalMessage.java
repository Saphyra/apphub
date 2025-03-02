package com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.EdConflict;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.Faction;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.ThargoidWar;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FsdJumpJournalMessage extends JournalMessage {
    @JsonProperty("BodyType")
    private String bodyType;

    @JsonProperty("Conflicts")
    private EdConflict[] conflicts;

    @JsonProperty("ControllingPower")
    private String controllingPower;

    @JsonProperty("PowerplayState")
    private String powerplayState;

    @JsonProperty("Powers")
    private String[] powers;

    @JsonProperty("Factions")
    private Faction[] factions;

    @JsonProperty("Population")
    private Long population;

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

    //Unused
    @JsonProperty("SystemGovernment")
    private String government;

    @JsonProperty("Multicrew")
    private Boolean multicrew;

    @JsonProperty("Taxi")
    private Boolean taxi;

    @JsonProperty("ThargoidWar")
    private ThargoidWar thargoidWar;

    @JsonProperty("FactionState")
    private String factionState;
}
