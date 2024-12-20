import { Endpoint, RequestMethod } from "../../dao";

export const SKYXPLORE_PLATFORM_HAS_CHARACTER = new Endpoint(RequestMethod.GET, "/api/skyxplore/data/character/exists");
export const SKYXPLORE_GET_CHARACTER_NAME = new Endpoint(RequestMethod.GET, "/api/skyxplore/data/character/name");
export const SKYXPLORE_CREATE_OR_UPDATE_CHARACTER = new Endpoint(RequestMethod.POST, "/api/skyxplore/data/character");
export const SKYXPLORE_GET_GAMES = new Endpoint(RequestMethod.GET, "/api/skyxplore/data/saved-game");
export const SKYXPLORE_SEARCH_FOR_FRIENDS = new Endpoint(RequestMethod.POST, "/api/skyxplore/data/friend/candidate");
export const SKYXPLORE_ADD_FRIEND = new Endpoint(RequestMethod.PUT, "/api/skyxplore/data/friend/request");
export const SKYXPLORE_GET_INCOMING_FRIEND_REQUEST = new Endpoint(RequestMethod.GET, "/api/skyxplore/data/friend/request/incoming");
export const SKYXPLORE_GET_SENT_FRIEND_REQUEST = new Endpoint(RequestMethod.GET, "/api/skyxplore/data/friend/request/sent");
export const SKYXPLORE_CANCEL_FRIEND_REQUEST = new Endpoint(RequestMethod.DELETE, "/api/skyxplore/data/friend/request/{friendRequestId}");
export const SKYXPLORE_ACCEPT_FRIEND_REQUEST = new Endpoint(RequestMethod.POST, "/api/skyxplore/data/friend/request/{friendRequestId}");
export const SKYXPLORE_GET_FRIENDS = new Endpoint(RequestMethod.GET, "/api/skyxplore/data/friend");
export const SKYXPLORE_REMOVE_FRIEND = new Endpoint(RequestMethod.DELETE, "/api/skyxplore/data/friend/{friendshipId}");
export const SKYXPLORE_DATA_GET_SETTING = new Endpoint(RequestMethod.POST, "/api/skyxplore/data/setting");
export const SKYXPLORE_DATA_CREATE_SETTING = new Endpoint(RequestMethod.PUT, "/api/skyxplore/data/setting");
export const SKYXPLORE_DATA_DELETE_SETTING = new Endpoint(RequestMethod.DELETE, "/api/skyxplore/data/setting");

//SkyXplore Game Data
export const SKYXPLORE_GET_ITEM_DATA = new Endpoint(RequestMethod.GET, "/api/skyxplore/data/data/{dataId}");
export const SKYXPLORE_DATA_TERRAFORMING_POSSIBILITIES = new Endpoint(RequestMethod.GET, "/api/skyxplore/data/{surfaceType}/terraforming-possibilities");
export const SKYXPLORE_DATA_CONSTRUCTION_AREAS = new Endpoint(RequestMethod.GET, "/api/skyxplore/data/{surfaceType}/construction-area");
export const SKYXPLORE_DATA_AVAILABLE_BUILDINGS = new Endpoint(RequestMethod.GET, "/api/skyxplore/data/data/{surfaceType}/buildings");
export const SKYXPLORE_DATA_CITIZEN_STATS_AND_SKILLS = new Endpoint(RequestMethod.GET, "/api/skyxplore/data/citizen/stats-and-skills");
export const SKYXPLORE_DATA_RESOURCES = new Endpoint(RequestMethod.GET, "/api/skyxplore/data/resources");