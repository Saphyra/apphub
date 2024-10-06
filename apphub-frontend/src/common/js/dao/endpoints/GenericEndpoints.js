import { Endpoint, RequestMethod } from "../dao";

export const CHECK_SESSION = new Endpoint(RequestMethod.GET, "/api/user/authentication/session");
export const GET_OWN_USER_ID = new Endpoint(RequestMethod.GET, "/user/id");
export const USER_DATA_SEARCH_ACCOUNT = new Endpoint(RequestMethod.POST, "/api/user/accounts");