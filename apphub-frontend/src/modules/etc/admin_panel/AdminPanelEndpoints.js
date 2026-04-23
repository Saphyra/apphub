import { Endpoint, RequestMethod } from "common/js/dao/dao";

export const ADMIN_PANEL_MIGRATION_GET_TASKS = new Endpoint(RequestMethod.GET, "/api/admin-panel/migration");
export const ADMIN_PANEL_MIGRATION_DELETE_TASK = new Endpoint(RequestMethod.DELETE, "/api/admin-panel/migration/{event}");
export const ADMIN_PANEL_MIGRATION_TRIGGER_TASK = new Endpoint(RequestMethod.POST, "/api/admin-panel/migration/{event}");

export const ADMIN_PANEL_GET_ERROR_REPORTS = new Endpoint(RequestMethod.POST, "/api/admin-panel/error-report");
export const ADMIN_PANEL_ERROR_REPORT_DELETE_ALL = new Endpoint(RequestMethod.DELETE, "/api/admin-panel/error-report/all");
export const ADMIN_PANEL_ERROR_REPORT_DELETE_READ = new Endpoint(RequestMethod.DELETE, "/api/admin-panel/error-report/read");
export const ADMIN_PANEL_DELETE_ERROR_REPORTS = new Endpoint(RequestMethod.DELETE, "/api/admin-panel/error-report");
export const ADMIN_PANEL_MARK_ERROR_REPORTS = new Endpoint(RequestMethod.POST, "/api/admin-panel/error-report/mark/{status}");
export const ADMIN_PANEL_ERROR_REPORT_DETAILS_PAGE = new Endpoint(RequestMethod.GET, "/web/admin-panel/error-report/{id}");
export const ADMIN_PANEL_GET_ERROR_REPORT = new Endpoint(RequestMethod.GET, "/api/admin-panel/error-report/{id}");

//Roles
export const USER_DATA_ROLES_FOR_ALL_RESTRICTED = new Endpoint(RequestMethod.GET, "/api/user/data/roles/restricted");
export const ADMIN_PANEL_AVAILABLE_ROLES = new Endpoint(RequestMethod.GET, "/api/user/data/roles/restricted");
export const USER_DATA_ADD_ROLE_TO_ALL = new Endpoint(RequestMethod.POST, "/api/user/data/roles/all/{role}");
export const USER_DATA_REMOVE_ROLE_FROM_ALL = new Endpoint(RequestMethod.DELETE, "/api/user/data/roles/all/{role}");
export const USER_DATA_GET_USER_ROLES = new Endpoint(RequestMethod.POST, "/api/user/data/roles");
export const USER_DATA_ADD_ROLE = new Endpoint(RequestMethod.PUT, "/api/user/data/roles");
export const USER_DATA_REMOVE_ROLE = new Endpoint(RequestMethod.DELETE, "/api/user/data/roles");
export const USER_DATA_GET_DISABLED_ROLES = new Endpoint(RequestMethod.GET, "/api/user/data/roles/disabled");
export const USER_DATA_ENABLE_ROLE = new Endpoint(RequestMethod.DELETE, "/api/user/data/roles/{role}");
export const USER_DATA_DISABLE_ROLE = new Endpoint(RequestMethod.PUT, "/api/user/data/roles/{role}");

//Ban
export const ACCOUNT_BAN_SEARCH = new Endpoint(RequestMethod.POST, "/api/user/ban/search");
export const ADMIN_PANEL_BAN_DETAILS_PAGE = new Endpoint(RequestMethod.GET, "/web/admin-panel/ban/{userId}");
export const ACCOUNT_GET_BANS = new Endpoint(RequestMethod.GET, "/api/user/ban/{userId}");
export const ACCOUNT_MARK_FOR_DELETION = new Endpoint(RequestMethod.DELETE, "/api/user/ban/{userId}/mark-for-deletion");
export const ACCOUNT_UNMARK_FOR_DELETION = new Endpoint(RequestMethod.POST, "/api/user/ban/{userId}/mark-for-deletion");
export const ACCOUNT_BAN_USER = new Endpoint(RequestMethod.PUT, "/api/user/ban");
export const ACCOUNT_REVOKE_BAN = new Endpoint(RequestMethod.DELETE, "/api/user/ban/{banId}");
export const ACCOUNT_BAN_GET_DETAILS_FOR_ERROR_PAGE = new Endpoint(RequestMethod.POST, "/api/user/ban/details");