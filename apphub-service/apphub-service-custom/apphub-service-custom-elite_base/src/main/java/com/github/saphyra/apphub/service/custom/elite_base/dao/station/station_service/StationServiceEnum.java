package com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service;

import java.util.Arrays;
import java.util.List;

import static io.micrometer.common.util.StringUtils.isBlank;

public enum StationServiceEnum {
    DOCK("Dock"),
    AUTO_DOCK("autodock"),
    COMMODITIES("commodities"),
    BLACK_MARKET("blackmarket"),
    CONTACTS("contacts"),
    MISSIONS("missions"),
    EXPLORATION("exploration"),
    OUTFITTING("outfitting"),
    CREW_LOUNGE("crewlounge"),
    REARM("rearm"),
    REFUEL("refuel"),
    REPAIR("repair"),
    TUNING("tuning"),
    ENGINEER("engineer"),
    FACILITATOR("facilitator"),
    FLIGHT_CONTROLLER("flightcontroller"),
    STATION_OPERATIONS("stationoperations"),
    SEARCH_AND_RESCUE(List.of("searchrescue", "SearchAndRescue")),
    STATION_MENU("stationMenu"),
    SHOP("shop"),
    LIVERY("livery"),
    SHIPYARD("shipyard"),
    MISSION_GENERATED("missionsgenerated"),
    POWERPLAY("powerplay"),
    ON_DOCK_MISSION("ondockmission"),
    SOCIAL_SPACE("socialspace"),
    BARTENDER("bartender"),
    VISTA_GENOMICS("vistagenomics"),
    PIONEER_SUPPLIES("pioneersupplies"),
    APEX_INTERSTELLAR("apexinterstellar"),
    FRONTLINE_SOLUTIONS("frontlinesolutions"),
    TECH_BROKER("techBroker"),
    MATERIAL_TRADER("materialtrader"),
    CARRIER_MANAGEMENT("carriermanagement"),
    MODULE_PACKS("modulepacks"),
    CARRIER_FUEL("carrierfuel"),
    VOUCHER_REDEMPTION("voucherredemption"),
    CARRIER_VENDOR("carriervendor"),
    WORKSHOP("Workshop"),
    ;

    private final List<String> value;

    StationServiceEnum(String value) {
        this.value = List.of(value.toLowerCase());
    }

    StationServiceEnum(List<String> values) {
        this.value = values.stream()
            .map(String::toLowerCase)
            .toList();
    }

    public String getValue() {
        return value.get(0);
    }

    public static StationServiceEnum parse(String in) {
        if (isBlank(in)) {
            return null;
        }

        return Arrays.stream(values())
            .filter(e -> e.value.contains(in.toLowerCase()))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Could not parse " + in + " to " + StationServiceEnum.class.getSimpleName()));
    }
}
