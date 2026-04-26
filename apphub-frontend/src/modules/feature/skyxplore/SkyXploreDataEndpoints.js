import Endpoint from "common/js/dao/Endpoint";
import RequestMethod from "common/js/dao/RequestMethod";

//SkyXplore Game Data
export const SKYXPLORE_GET_ITEM_DATA = new Endpoint(RequestMethod.GET, "/api/skyxplore/data/data/{dataId}");
export const SKYXPLORE_DATA_TERRAFORMING_POSSIBILITIES = new Endpoint(RequestMethod.GET, "/api/skyxplore/data/{surfaceType}/terraforming-possibilities");
export const SKYXPLORE_DATA_CONSTRUCTION_AREAS = new Endpoint(RequestMethod.GET, "/api/skyxplore/data/{surfaceType}/construction-area");
export const SKYXPLORE_DATA_AVAILABLE_BUILDINGS = new Endpoint(RequestMethod.GET, "/api/skyxplore/data/data/{surfaceType}/buildings");
export const SKYXPLORE_DATA_CITIZEN_STATS_AND_SKILLS = new Endpoint(RequestMethod.GET, "/api/skyxplore/data/citizen/stats-and-skills");
export const SKYXPLORE_DATA_RESOURCES = new Endpoint(RequestMethod.GET, "/api/skyxplore/data/resources");