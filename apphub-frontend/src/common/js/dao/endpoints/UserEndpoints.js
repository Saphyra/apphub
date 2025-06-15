import { Endpoint, RequestMethod } from "../dao";

export const USER_DATA_GET_USERNAME = new Endpoint(RequestMethod.GET, "/api/user/data/name");
export const ACCOUNT_CHANGE_LANGUAGE = new Endpoint(RequestMethod.POST, "/api/user/account/language");
export const ACCOUNT_CHANGE_EMAIL = new Endpoint(RequestMethod.POST, "/api/user/account/email");
export const ACCOUNT_CHANGE_USERNAME = new Endpoint(RequestMethod.POST, "/api/user/account/username");
export const ACCOUNT_CHANGE_PASSWORD = new Endpoint(RequestMethod.POST, "/api/user/account/password");
export const ACCOUNT_DELETE_ACCOUNT = new Endpoint(RequestMethod.DELETE, "/api/user/account");
export const ACCOUNT_GET_USER = new Endpoint(RequestMethod.GET, "/api/user/account");

export const ACCOUNT_REGISTER = new Endpoint(RequestMethod.POST, "/api/user");
export const LOGIN = new Endpoint(RequestMethod.POST, "/api/user/authentication/login");
export const LOGOUT = new Endpoint(RequestMethod.POST, "/api/user/authentication/logout");

export const GET_USER_SETTINGS = new Endpoint(RequestMethod.GET, "/api/user/settings/{category}");
export const SET_USER_SETTINGS = new Endpoint(RequestMethod.POST, "/api/user/settings");

export const USER_DATA_ROLES_FOR_ALL_RESTRICTED = new Endpoint(RequestMethod.GET, "/api/user/data/roles/restricted");
export const ADMIN_PANEL_AVAILABLE_ROLES = new Endpoint(RequestMethod.GET, "/api/user/data/roles/restricted");
export const USER_DATA_ADD_ROLE_TO_ALL = new Endpoint(RequestMethod.POST, "/api/user/data/roles/all/{role}");
export const USER_DATA_REMOVE_ROLE_FROM_ALL = new Endpoint(RequestMethod.DELETE, "/api/user/data/roles/all/{role}");

export const USER_DATA_GET_USER_ROLES = new Endpoint(RequestMethod.POST, "/api/user/data/roles");
export const USER_DATA_ADD_ROLE = new Endpoint(RequestMethod.PUT, "/api/user/data/roles");
export const USER_DATA_REMOVE_ROLE = new Endpoint(RequestMethod.DELETE, "/api/user/data/roles");
export const IS_ADMIN = new Endpoint(RequestMethod.GET, "/api/user/admin");

export const USER_DATA_GET_DISABLED_ROLES = new Endpoint(RequestMethod.GET, "/api/user/data/roles/disabled");
export const USER_DATA_ENABLE_ROLE = new Endpoint(RequestMethod.DELETE, "/api/user/data/roles/{role}");
export const USER_DATA_DISABLE_ROLE = new Endpoint(RequestMethod.PUT, "/api/user/data/roles/{role}");

export const ACCOUNT_BAN_SEARCH = new Endpoint(RequestMethod.POST, "/api/user/ban/search");
export const ADMIN_PANEL_BAN_DETAILS_PAGE = new Endpoint(RequestMethod.GET, "/web/admin-panel/ban/{userId}");
export const ACCOUNT_GET_BANS = new Endpoint(RequestMethod.GET, "/api/user/ban/{userId}");
export const ACCOUNT_MARK_FOR_DELETION = new Endpoint(RequestMethod.DELETE, "/api/user/ban/{userId}/mark-for-deletion");
export const ACCOUNT_UNMARK_FOR_DELETION = new Endpoint(RequestMethod.POST, "/api/user/ban/{userId}/mark-for-deletion");
export const ACCOUNT_BAN_USER = new Endpoint(RequestMethod.PUT, "/api/user/ban");
export const ACCOUNT_REVOKE_BAN = new Endpoint(RequestMethod.DELETE, "/api/user/ban/{banId}");
export const ACCOUNT_BAN_GET_DETAILS_FOR_ERROR_PAGE = new Endpoint(RequestMethod.POST, "/api/user/ban/details");