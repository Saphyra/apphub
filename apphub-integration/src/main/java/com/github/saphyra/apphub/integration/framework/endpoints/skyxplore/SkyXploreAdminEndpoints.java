package com.github.saphyra.apphub.integration.framework.endpoints.skyxplore;

public class SkyXploreAdminEndpoints {
    public static final String SKYXPLORE_GAME_ADMIN_MAIN_PAGE = "/web/skyxplore/game/admin";
    public static final String SKYXPLORE_GAME_ADMIN_LIST_PAGE = "/web/skyxplore/game/admin/{type}/{gameId}";
    public static final String SKYXPLORE_GAME_ADMIN_DETAILS_PAGE = "/web/skyxplore/game/admin/{gameId}/{type}/{itemId}";

    public static final String SKYXPLORE_GAME_ADMIN_GET_BY_TYPE = "/api/skyxplore/game/admin/{type}";
    public static final String SKYXPLORE_GAME_ADMIN_GET_ITEM = "/api/skyxplore/game/admin/{gameId}/{type}/{itemId}";
}
