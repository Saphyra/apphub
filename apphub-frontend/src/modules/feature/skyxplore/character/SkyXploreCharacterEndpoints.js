import Endpoint from "common/js/dao/Endpoint";
import RequestMethod from "common/js/dao/RequestMethod";

export const SKYXPLORE_CHARACTER_PAGE = "/web/skyxplore/character";

export const SKYXPLORE_PLATFORM_HAS_CHARACTER = new Endpoint(RequestMethod.GET, "/api/skyxplore/data/character/exists");
export const SKYXPLORE_GET_CHARACTER_NAME = new Endpoint(RequestMethod.GET, "/api/skyxplore/data/character/name");
export const SKYXPLORE_CREATE_OR_UPDATE_CHARACTER = new Endpoint(RequestMethod.POST, "/api/skyxplore/data/character");