import Endpoint from "common/js/dao/Endpoint";
import RequestMethod from "common/js/dao/RequestMethod";

export const MODULES_PAGE = "/web/modules";

export const MODULES_GET = new Endpoint(RequestMethod.GET, "/api/modules");
export const MODULES_SET_FAVORITE = new Endpoint(RequestMethod.POST, "/api/modules/{module}/favorite");