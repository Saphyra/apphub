import Endpoint from "common/js/dao/Endpoint";
import RequestMethod from "common/js/dao/RequestMethod";

export const ACCOUNT_REGISTER = new Endpoint(RequestMethod.POST, "/api/user");
export const LOGIN = new Endpoint(RequestMethod.POST, "/api/authorization/login");
export const LOGOUT = new Endpoint(RequestMethod.POST, "/api/authorization/logout");
export const USER_DATA_GET_ACCOUNT = new Endpoint(RequestMethod.GET, "/api/user/account");