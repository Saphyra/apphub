package com.github.saphyra.apphub.service.feature.elite_base.processor.dao;

import java.util.List;

public enum ObjectType {
    COMMODITY,
    FC_MATERIAL,
    EQUIPMENT,
    SPACESHIP,
    STAR_SYSTEM,
    FLEET_CARRIER,
    BODY,
    BODY_DATA,
    COMMODITY_AVERAGE_PRICE,
    MINOR_FACTION,
    STATION,
    ;

    public static final List<ObjectType> TRADING_TYPES = List.of(COMMODITY);
}
