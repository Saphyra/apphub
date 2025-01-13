package com.github.saphyra.apphub.service.elite_base.message_processing.dao.fleet_carrier;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
public enum FleetCarrierDockingAccess {
    ALL("all"),
    SQUADRON_AND_FRIENDS("squadronfriends"),
    FRIENDS("friends"),
    SQUADRON("squadron"),
    NONE("none"),
    ;

    private final String value;

    public static FleetCarrierDockingAccess parse(String in) {
        if (isNull(in)) {
            return null;
        }

        return Arrays.stream(values())
            .filter(e -> e.value.equalsIgnoreCase(in))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException(in + " cannot be parsed to " + FleetCarrierDockingAccess.class.getSimpleName()));
    }
}
