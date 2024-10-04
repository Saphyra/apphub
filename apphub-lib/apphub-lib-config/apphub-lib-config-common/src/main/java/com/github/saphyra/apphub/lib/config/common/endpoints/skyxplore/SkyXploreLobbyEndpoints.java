package com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore;

public class SkyXploreLobbyEndpoints {
    public static final String SKYXPLORE_INTERNAL_GAME_LOADED = "/allowed-internal/skyxplore/lobby/{gameId}/loaded";

    public static final String SKYXPLORE_LOBBY_VIEW_FOR_PAGE = "/api/skyxplore/lobby/page";
    public static final String SKYXPLORE_LOBBY_IS_IN_LOBBY = "/api/skyxplore/lobby";
    public static final String SKYXPLORE_EXIT_FROM_LOBBY = "/api/skyxplore/lobby";
    public static final String SKYXPLORE_INVITE_TO_LOBBY = "/api/skyxplore/lobby/invite/{friendId}";
    public static final String SKYXPLORE_LOBBY_ACCEPT_INVITATION = "/api/skyxplore/lobby/join/{invitorId}";
    public static final String SKYXPLORE_LOBBY_GET_PLAYERS = "/api/skyxplore/lobby/players";
    public static final String SKYXPLORE_LOBBY_GET_ALLIANCES = "/api/skyxplore/lobby/alliances";
    public static final String SKYXPLORE_CREATE_LOBBY = "/api/skyxplore/lobby";
    public static final String SKYXPLORE_LOBBY_START_GAME = "/api/skyxplore/lobby/start";
    public static final String SKYXPLORE_LOBBY_GET_ACTIVE_FRIENDS = "/api/skyxplore/lobby/friends/active";
    public static final String SKYXPLORE_LOBBY_LOAD_GAME = "/api/skyxplore/lobby/load-game/{gameId}";
    public static final String SKYXPLORE_LOBBY_EDIT_SETTINGS = "/api/skyxplore/lobby/settings";
    public static final String SKYXPLORE_LOBBY_GET_SETTINGS = "/api/skyxplore/lobby/settings";
    public static final String SKYXPLORE_LOBBY_CREATE_OR_MODIFY_AI = "/api/skyxplore/lobby/ai";
    public static final String SKYXPLORE_LOBBY_REMOVE_AI = "/api/skyxplore/lobby/ai/{userId}";
    public static final String SKYXPLORE_LOBBY_GET_AIS = "/api/skyxplore/lobby/ai";
    public static final String SKYXPLORE_LOBBY_CHANGE_ALLIANCE_OF_PLAYER = "/api/skyxplore/lobby/alliance/player/{userId}";
    public static final String SKYXPLORE_LOBBY_CHANGE_ALLIANCE_OF_AI = "/api/skyxplore/lobby/alliance/ai/{userId}";
}
