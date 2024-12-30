package com.github.saphyra.apphub.service.elite_base.dao.minor_faction;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static io.micrometer.common.util.StringUtils.isBlank;

@RequiredArgsConstructor
public enum FactionState {
    NONE(""),
    PIRATE_ATTACK("PirateAttack"),
    EXPANSION("Expansion"),
    INVESTMENT("Investment"),
    INFRASTRUCTURE_FAILURE("InfrastructureFailure"),
    ELECTION("Election"),
    BOOM("Boom"),
    WAR("War"),
    CIVIL_UNREST("CivilUnrest"),
    CIVIL_WAR("CivilWar"),
    BUST("Bust"),
    FAMINE("Famine"),
    OUTBREAK("Outbreak"),
    PUBLIC_HOLIDAY("PublicHoliday"),
    DROUGHT("Drought"),
    LOCKDOWN("Lockdown"),
    RETREAT("Retreat"),
    ;

    private final String value;

    //TODO unit test
    public static FactionState parse(String in) {
        if (isBlank(in)) {
            return NONE;
        }

        return Arrays.stream(values())
            .filter(factionState -> factionState.value.equalsIgnoreCase(in))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Could not parse " + in + " to " + FactionState.class.getSimpleName()));
    }
}
