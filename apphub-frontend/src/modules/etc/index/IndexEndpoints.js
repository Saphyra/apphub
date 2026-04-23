import { Endpoint, RequestMethod } from "common/js/dao/dao";

export const ACCOUNT_REGISTER = new Endpoint(RequestMethod.POST, "/api/user");
export const LOGIN = new Endpoint(RequestMethod.POST, "/api/user/authentication/login");
export const LOGOUT = new Endpoint(RequestMethod.POST, "/api/user/authentication/logout");