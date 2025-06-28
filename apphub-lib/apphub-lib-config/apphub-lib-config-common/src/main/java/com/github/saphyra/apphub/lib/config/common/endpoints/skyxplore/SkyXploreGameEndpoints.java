package com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore;

public class SkyXploreGameEndpoints {
    public static final String SKYXPLORE_INTERNAL_CREATE_GAME = "/internal/skyxplore/game";
    public static final String SKYXPLORE_INTERNAL_LOAD_GAME = "/internal/skyxplore/game";
    public static final String SKYXPLORE_INTERNAL_DELETE_GAME = "/internal/skyxplore/game/{gameId}";
    public static final String SKYXPLORE_GAME_SAVE = "/api/skyxplore/game";
    public static final String SKYXPLORE_GAME_IS_HOST = "/api/skyxplore/game/host";
    public static final String SKYXPLORE_GET_GAME_ID_OF_USER = "/api/skyxplore/game";
    public static final String SKYXPLORE_GAME_PAUSE = "/api/skyxplore/game/pause";
    public static final String SKYXPLORE_GAME_GET_PLAYERS = "/api/skyxplore/game/player";
    public static final String SKYXPLORE_EXIT_GAME = "/api/skyxplore/game";
    public static final String SKYXPLORE_PROCESS_TICK = "/api/skyxplore/game/tick";

    //Chat
    public static final String SKYXPLORE_GAME_CREATE_CHAT_ROOM = "/api/skyxplore/game/chat/room";
    public static final String SKYXPLORE_GAME_LEAVE_CHAT_ROOM = "/api/skyxplore/game/chat/room/{roomId}";
    public static final String SKYXPLORE_GAME_GET_CHAT_ROOMS = "/api/skyxplore/game/chat/room";

    //Map
    public static final String SKYXPLORE_GAME_MAP = "/api/skyxplore/game/universe";

    //Solar System
    public static final String SKYXPLORE_GET_SOLAR_SYSTEM = "/api/skyxplore/game/solar-system/{solarSystemId}";
    public static final String SKYXPLORE_SOLAR_SYSTEM_RENAME = "/api/skyxplore/game/solar-system/{solarSystemId}/name";

    //Planet
    public static final String SKYXPLORE_PLANET_GET_OVERVIEW = "/api/skyxplore/game/planet/{planetId}/overview";
    public static final String SKYXPLORE_PLANET_GET_POPULATION = "/api/skyxplore/game/planet/{planetId}/citizen";
    public static final String SKYXPLORE_PLANET_UPDATE_PRIORITY = "/api/skyxplore/game/planet/{planetId}/priority/{priorityType}";
    public static final String SKYXPLORE_PLANET_RENAME = "/api/skyxplore/game/planet/{planetId}/name";

    //Storage Settings
    public static final String SKYXPLORE_PLANET_GET_STORAGE_SETTINGS = "/api/skyxplore/game/planet/{planetId}/storage-settings";
    public static final String SKYXPLORE_PLANET_CREATE_STORAGE_SETTING = "/api/skyxplore/game/planet/{planetId}/storage-settings";
    public static final String SKYXPLORE_PLANET_DELETE_STORAGE_SETTING = "/api/skyxplore/game/storage-settings/{storageSettingId}";
    public static final String SKYXPLORE_PLANET_EDIT_STORAGE_SETTING = "/api/skyxplore/game/storage-settings";

    //Citizen
    public static final String SKYXPLORE_PLANET_RENAME_CITIZEN = "/api/skyxplore/game/citizen/{citizenId}/rename";

    //Building
    @Deprecated(forRemoval = true)
    public static final String SKYXPLORE_BUILDING_CONSTRUCT_NEW = "/api/skyxplore/game/building/{planetId}/{surfaceId}";
    @Deprecated(forRemoval = true)
    public static final String SKYXPLORE_BUILDING_UPGRADE = "/api/skyxplore/game/building/{planetId}/{buildingId}";
    @Deprecated(forRemoval = true)
    public static final String SKYXPLORE_BUILDING_CANCEL_CONSTRUCTION = "/api/skyxplore/game/building/{planetId}/{buildingId}";
    @Deprecated(forRemoval = true)
    public static final String SKYXPLORE_BUILDING_DECONSTRUCT = "/api/skyxplore/game/building/{planetId}/{buildingId}/deconstruct";
    @Deprecated(forRemoval = true)
    public static final String SKYXPLORE_BUILDING_CANCEL_DECONSTRUCTION = "/api/skyxplore/game/building/{planetId}/{buildingId}/deconstruct";

    //Terraformation
    public static final String SKYXPLORE_GAME_TERRAFORM_SURFACE = "/api/skyxplore/game/surface/{planetId}/{surfaceId}/terraform";
    public static final String SKYXPLORE_GAME_CANCEL_TERRAFORMATION = "/api/skyxplore/game/surface/{planetId}/{surfaceId}/terraform";

    //Construction Area
    public static final String SKYXPLORE_PLANET_SURFACE_CONSTRUCT_CONSTRUCTION_AREA = "/api/skyxplore/game/surface/{surfaceId}/construction-area";
    public static final String SKYXPLORE_PLANET_SURFACE_CANCEL_CONSTRUCTION_AREA_CONSTRUCTION = "/api/skyxplore/game/surface/construction-area/{constructionId}/cancel-construction";
    public static final String SKYXPLORE_PLANET_SURFACE_DECONSTRUCT_CONSTRUCTION_AREA = "/api/skyxplore/game/surface/construction-area/{constructionAreaId}";
    public static final String SKYXPLORE_PLANET_SURFACE_CANCEL_DECONSTRUCT_CONSTRUCTION_AREA = "/api/skyxplore/game/surface/construction-area/{deconstructionId}/cancel-deconstruction";

    //Building Module
    public static final String SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_GET_BUILDING_MODULES = "/api/skyxplore/game/surface/construction-area/{constructionAreaId}/building-module";
    public static final String SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_CONSTRUCT_BUILDING_MODULE = "/api/skyxplore/game/surface/construction-area/{constructionAreaId}/building-module";
    public static final String SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_CANCEL_CONSTRUCTION_OF_BUILDING_MODULE = "/api/skyxplore/game/surface/construction-area/building-module/{constructionId}/cancel-construction";
    public static final String SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_DECONSTRUCT_BUILDING_MODULE = "/api/skyxplore/game/surface/construction-area/building-module/{buildingModuleId}/deconstruct";
    public static final String SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_CANCEL_DECONSTRUCTION_OF_BUILDING_MODULE = "/api/skyxplore/game/surface/construction-area/building-module/{deconstructionId}/cancel-deconstruction";
    public static final String SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_AVAILABLE_BUILDING_MODULES = "/api/skyxplore/game/surface/construction-area/{constructionAreaId}/building-modules/{buildingModuleCategory}";

    //Queue
    public static final String SKYXPLORE_PLANET_SET_QUEUE_ITEM_PRIORITY = "/api/skyxplore/game/{planetId}/{type}/{itemId}/priority";
    public static final String SKYXPLORE_PLANET_CANCEL_QUEUE_ITEM = "/api/skyxplore/game/{planetId}/{type}/{itemId}";
}
