package com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore;

public class GenericSkyXploreEndpoints {
    public static final String SKYXPLORE_MAIN_MENU_PAGE = "/web/skyxplore";

    //Events
    public static final String EVENT_SKYXPLORE_LOBBY_CLEANUP = "/event/skyxplore/lobby/cleanup";
    public static final String EVENT_SKYXPLORE_GAME_CLEANUP = "/event/skyxplore/game/cleanup";
    public static final String EVENT_SKYXPLORE_DELETE_GAMES = "/event/skyxplore/data/game/delete";

    //WebSocket
    public static final String WS_CONNECTION_SKYXPLORE_MAIN_MENU = "/api/ws/skyxplore-data/main-menu";
    public static final String WS_CONNECTION_SKYXPLORE_LOBBY = "/api/ws/skyxplore-lobby/lobby";
    public static final String WS_CONNECTION_SKYXPLORE_LOBBY_INVITATION = "/api/ws/skyxplore-lobby/invitation";
    public static final String WS_CONNECTION_SKYXPLORE_GAME = "/api/ws/skyxplore-game/game";
    public static final String WS_CONNECTION_SKYXPLORE_GAME_PLANET = "/api/ws/skyxplore-game/game/planet";
    public static final String WS_CONNECTION_SKYXPLORE_GAME_POPULATION = "/api/ws/skyxplore-game/game/population";
    public static final String WS_CONNECTION_SKYXPLORE_INTERNAL = "/allowed-internal/skyxplore";
}
