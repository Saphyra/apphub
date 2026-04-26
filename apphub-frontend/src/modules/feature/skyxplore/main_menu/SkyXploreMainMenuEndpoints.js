import Endpoint from "common/js/dao/Endpoint";
import RequestMethod from "common/js/dao/RequestMethod";

export const SKYXPLORE_MAIN_MENU_PAGE = "/web/skyxplore";

export const SKYXPLORE_GET_GAMES = new Endpoint(RequestMethod.GET, "/api/skyxplore/data/saved-game");
export const SKYXPLORE_SEARCH_FOR_FRIENDS = new Endpoint(RequestMethod.POST, "/api/skyxplore/data/friend/candidate");
export const SKYXPLORE_ADD_FRIEND = new Endpoint(RequestMethod.PUT, "/api/skyxplore/data/friend/request");
export const SKYXPLORE_GET_INCOMING_FRIEND_REQUEST = new Endpoint(RequestMethod.GET, "/api/skyxplore/data/friend/request/incoming");
export const SKYXPLORE_GET_SENT_FRIEND_REQUEST = new Endpoint(RequestMethod.GET, "/api/skyxplore/data/friend/request/sent");
export const SKYXPLORE_CANCEL_FRIEND_REQUEST = new Endpoint(RequestMethod.DELETE, "/api/skyxplore/data/friend/request/{friendRequestId}");
export const SKYXPLORE_ACCEPT_FRIEND_REQUEST = new Endpoint(RequestMethod.POST, "/api/skyxplore/data/friend/request/{friendRequestId}");
export const SKYXPLORE_GET_FRIENDS = new Endpoint(RequestMethod.GET, "/api/skyxplore/data/friend");
export const SKYXPLORE_REMOVE_FRIEND = new Endpoint(RequestMethod.DELETE, "/api/skyxplore/data/friend/{friendshipId}");