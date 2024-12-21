package com.github.saphyra.apphub.api.skyxplore.model.game;

public enum ProcessType {
    CONSTRUCT_CONSTRUCTION_AREA,
    DECONSTRUCT_CONSTRUCTION_AREA,
    CONSTRUCT_BUILDING_MODULE,
    DECONSTRUCT_BUILDING_MODULE,
    @Deprecated(forRemoval = true)
    CONSTRUCTION,
    @Deprecated(forRemoval = true)
    DECONSTRUCTION,
    TERRAFORMATION,
    PRODUCTION_ORDER,
    WORK,
    STORAGE_SETTING,
    REST,
}
