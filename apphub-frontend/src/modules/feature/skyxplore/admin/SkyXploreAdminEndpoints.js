import { Endpoint, RequestMethod } from "common/js/dao/dao";

export const SKYXPLORE_ADMIN_MAIN_PAGE = "/web/skyxplore/game/admin";
export const SKYXPLORE_ADMIN_DETAILS_PAGE = new Endpoint(RequestMethod.GET, "/web/skyxplore/game/admin/{gameId}/{type}/{id}");

export const SKYXPLORE_GAME_ADMIN_GET_BY_TYPE = new Endpoint(RequestMethod.GET, "/api/skyxplore/game/admin/{type}");
export const SKYXPLORE_GAME_ADMIN_GET_ITEM = new Endpoint(RequestMethod.GET, "/api/skyxplore/game/admin/{gameId}/{type}/{itemId}");
