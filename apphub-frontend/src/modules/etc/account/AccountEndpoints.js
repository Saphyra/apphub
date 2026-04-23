import { Endpoint, RequestMethod } from "common/js/dao/dao";

export const USER_DATA_GET_USERNAME = new Endpoint(RequestMethod.GET, "/api/user/data/name");
export const ACCOUNT_CHANGE_LANGUAGE = new Endpoint(RequestMethod.POST, "/api/user/account/language");
export const ACCOUNT_CHANGE_EMAIL = new Endpoint(RequestMethod.POST, "/api/user/account/email");
export const ACCOUNT_CHANGE_USERNAME = new Endpoint(RequestMethod.POST, "/api/user/account/username");
export const ACCOUNT_CHANGE_PASSWORD = new Endpoint(RequestMethod.POST, "/api/user/account/password");
export const ACCOUNT_DELETE_ACCOUNT = new Endpoint(RequestMethod.DELETE, "/api/user/account");
export const ACCOUNT_GET_USER = new Endpoint(RequestMethod.GET, "/api/user/account");