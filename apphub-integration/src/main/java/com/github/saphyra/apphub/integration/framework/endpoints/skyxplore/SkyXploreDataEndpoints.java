package com.github.saphyra.apphub.integration.framework.endpoints.skyxplore;

public class SkyXploreDataEndpoints {
    public static final String SKYXPLORE_CHARACTER_PAGE = "/web/skyxplore/character";
    public static final String SKYXPLORE_MAIN_MENU_PAGE = "/web/skyxplore";

    //Character
    public static final String SKYXPLORE_CHARACTER_EXISTS = "/api/skyxplore/data/character/exists";
    public static final String SKYXPLORE_CREATE_OR_UPDATE_CHARACTER = "/api/skyxplore/data/character";
    public static final String SKYXPLORE_GET_CHARACTER_NAME = "/api/skyxplore/data/character/name";

    //SKYXPLORE-DATA GAMES
    public static final String SKYXPLORE_GET_GAMES = "/api/skyxplore/data/saved-game";
    public static final String SKYXPLORE_DELETE_GAME = "/api/skyxplore/data/saved-game/{gameId}";

    //SKYXPLORE-DATA-FRIENDS
    public static final String SKYXPLORE_SEARCH_FOR_FRIENDS = "/api/skyxplore/data/friend/candidate";
    public static final String SKYXPLORE_ADD_FRIEND = "/api/skyxplore/data/friend/request";
    public static final String SKYXPLORE_GET_SENT_FRIEND_REQUEST = "/api/skyxplore/data/friend/request/sent";
    public static final String SKYXPLORE_GET_INCOMING_FRIEND_REQUEST = "/api/skyxplore/data/friend/request/incoming";
    public static final String SKYXPLORE_CANCEL_FRIEND_REQUEST = "/api/skyxplore/data/friend/request/{friendRequestId}";
    public static final String SKYXPLORE_ACCEPT_FRIEND_REQUEST = "/api/skyxplore/data/friend/request/{friendRequestId}";
    public static final String SKYXPLORE_GET_FRIENDS = "/api/skyxplore/data/friend";
    public static final String SKYXPLORE_REMOVE_FRIEND = "/api/skyxplore/data/friend/{friendshipId}";

    //SKYXPLORE-DATA RESOURCES
    public static final String SKYXPLORE_GET_ITEM_DATA = "/api/skyxplore/data/data/{dataId}";
    public static final String SKYXPLORE_DATA_CONSTRUCTION_AREAS = "/api/skyxplore/data/{surfaceType}/construction-area";

    //SKYXPLORE-DATA-SETTINGS
    public static final String SKYXPLORE_DATA_CREATE_SETTING = "/api/skyxplore/data/setting";
    public static final String SKYXPLORE_DATA_GET_SETTING = "/api/skyxplore/data/setting";
    public static final String SKYXPLORE_DATA_DELETE_SETTING = "/api/skyxplore/data/setting";
}
