package com.github.saphyra.apphub.service.elite_base.dao.fleet_carrier;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
public enum FleetCarrierDockingAccess {
    ALL("all"),
    SQUADRON_AND_FRIENDS("squadronfriends"),
    FRIENDS("friends"),
    NONE("none"),
    ;

    private final String value;

    //TODO unit test
    public static FleetCarrierDockingAccess parse(String in) {
        if (isNull(in)) {
            return null;
        }

        return Arrays.stream(values())
            .filter(e -> e.value.equalsIgnoreCase(in))
            .findAny()
            .orElseThrow(() -> new RuntimeException(in + " cannot be parsed to " + FleetCarrierDockingAccess.class.getSimpleName()));
    }
}
