import { Endpoint, RequestMethod } from "../../../../common/js/dao/dao";

export const ELITE_BASE_STAR_SYSTEMS_SEARCH = new Endpoint(RequestMethod.POST, "/api/elite-base/star-systems/search");
export const ELITE_BASE_NEAREST_MATERIAL_TRADERS = new Endpoint(RequestMethod.GET, "/api/elite-base/nearest/{starId}/material-traders/{materialType}/{page}");