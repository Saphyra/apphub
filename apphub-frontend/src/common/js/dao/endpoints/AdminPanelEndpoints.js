import { Endpoint, RequestMethod } from "../dao";

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