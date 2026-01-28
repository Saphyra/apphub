package com.github.saphyra.apphub.service.custom.elite_base.dao.item;

import java.util.List;

public enum ItemType {
    COMMODITY, FC_MATERIAL, EQUIPMENT, SPACESHIP;

    public static final List<ItemType> TRADING_TYPES = List.of(COMMODITY);
}
