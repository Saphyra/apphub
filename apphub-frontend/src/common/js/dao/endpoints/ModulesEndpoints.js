import { Endpoint, RequestMethod } from "../dao";

export const MODULES_GET = new Endpoint(RequestMethod.GET, "/api/modules");
export const MODULES_SET_FAVORITE = new Endpoint(RequestMethod.POST, "/api/modules/{module}/favorite");