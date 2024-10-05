import { Endpoint, RequestMethod } from "../../dao";

export const SKYXPLORE_GAME_GET_GAME_ID = new Endpoint(RequestMethod.GET, "/api/skyxplore/game");

export const SKYXPLORE_GAME_SAVE = new Endpoint(RequestMethod.POST, "/api/skyxplore/game");
export const SKYXPLORE_GAME_PAUSE = new Endpoint(RequestMethod.POST, "/api/skyxplore/game/pause");
export const SKYXPLORE_EXIT_GAME = new Endpoint(RequestMethod.DELETE, "/api/skyxplore/game");
export const SKYXPLORE_GAME_GET_CHAT_ROOMS = new Endpoint(RequestMethod.GET, "/api/skyxplore/game/chat/room");
export const SKYXPLORE_GAME_GET_PLAYERS = new Endpoint(RequestMethod.GET, "/api/skyxplore/game/player");
export const SKYXPLORE_GAME_CREATE_CHAT_ROOM = new Endpoint(RequestMethod.PUT, "/api/skyxplore/game/chat/room");
export const SKYXPLORE_GAME_LEAVE_CHAT_ROOM = new Endpoint(RequestMethod.DELETE, "/api/skyxplore/game/chat/room/{roomId}");
export const SKYXPLORE_GAME_MAP = new Endpoint(RequestMethod.GET, "/api/skyxplore/game/universe");
export const SKYXPLORE_GET_SOLAR_SYSTEM = new Endpoint(RequestMethod.GET, "/api/skyxplore/game/solar-system/{solarSystemId}");
export const SKYXPLORE_SOLAR_SYSTEM_RENAME = new Endpoint(RequestMethod.POST, "/api/skyxplore/game/solar-system/{solarSystemId}/name");
export const SKYXPLORE_PLANET_RENAME = new Endpoint(RequestMethod.POST, "/api/skyxplore/game/planet/{planetId}/name");
export const SKYXPLORE_PLANET_GET_OVERVIEW = new Endpoint(RequestMethod.GET, "/api/skyxplore/game/planet/{planetId}/overview");
export const SKYXPLORE_PLANET_UPDATE_PRIORITY = new Endpoint(RequestMethod.POST, "/api/skyxplore/game/planet/{planetId}/priority/{priorityType}");
export const SKYXPLORE_BUILDING_DECONSTRUCT = new Endpoint(RequestMethod.POST, "/api/skyxplore/game/building/{planetId}/{buildingId}/deconstruct");
export const SKYXPLORE_BUILDING_CANCEL_DECONSTRUCTION = new Endpoint(RequestMethod.DELETE, "/api/skyxplore/game/building/{planetId}/{buildingId}/deconstruct");
export const SKYXPLORE_BUILDING_UPGRADE = new Endpoint(RequestMethod.POST, "/api/skyxplore/game/building/{planetId}/{buildingId}");
export const SKYXPLORE_BUILDING_CANCEL_CONSTRUCTION = new Endpoint(RequestMethod.DELETE, "/api/skyxplore/game/building/{planetId}/{buildingId}");
export const SKYXPLORE_PLANET_GET_POPULATION = new Endpoint(RequestMethod.GET, "/api/skyxplore/game/planet/{planetId}/citizen");
export const SKYXPLORE_PLANET_RENAME_CITIZEN = new Endpoint(RequestMethod.POST, "/api/skyxplore/game/citizen/{citizenId}/rename");
export const SKYXPLORE_PLANET_GET_STORAGE_SETTINGS = new Endpoint(RequestMethod.GET, "/api/skyxplore/game/planet/{planetId}/storage-settings");
export const SKYXPLORE_PLANET_CREATE_STORAGE_SETTING = new Endpoint(RequestMethod.PUT, "/api/skyxplore/game/planet/{planetId}/storage-settings");
export const SKYXPLORE_PLANET_EDIT_STORAGE_SETTING = new Endpoint(RequestMethod.POST, "/api/skyxplore/game/storage-settings");
export const SKYXPLORE_PLANET_DELETE_STORAGE_SETTING = new Endpoint(RequestMethod.DELETE, "/api/skyxplore/game/storage-settings/{storageSettingId}");
export const SKYXPLORE_GAME_IS_HOST = new Endpoint(RequestMethod.GET, "/api/skyxplore/game/host");