package com.github.saphyra.apphub.service.feature.elite_base.dao;

import java.util.List;

public enum ObjectType {
    COMMODITY,
    FC_MATERIAL,
    EQUIPMENT,
    SPACESHIP,
    STAR_SYSTEM,
    BODY,
    ;

    public static final List<ObjectType> TRADING_TYPES = List.of(COMMODITY);
}
