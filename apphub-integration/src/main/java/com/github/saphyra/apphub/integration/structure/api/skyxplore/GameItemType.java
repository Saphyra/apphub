package com.github.saphyra.apphub.integration.structure.api.skyxplore;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GameItemType {
    GAME,
    PLAYER,
    ALLIANCE,
    SOLAR_SYSTEM,
    PLANET,
    CITIZEN,
    SKILL,
    SURFACE,
    BUILDING_MODULE,
    CONSTRUCTION,
    CONSTRUCTION_AREA,
    DECONSTRUCTION,
    RESERVED_STORAGE,
    STORED_RESOURCE,
    STORAGE_SETTING,
    PRIORITY,
    COORDINATE,
    LINE,
    PRODUCTION_ORDER,
    DURABILITY,
    PROCESS,
    CITIZEN_ALLOCATION,
    BUILDING_MODULE_ALLOCATION,
    RESOURCE_DELIVERY_REQUEST,
    CONVOY,
    PRODUCTION_REQUEST,
}
