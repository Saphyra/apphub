import Endpoint from "common/js/dao/Endpoint";
import RequestMethod from "common/js/dao/RequestMethod";

export const TASK_MANAGER_PAGE = "/web/task-manager";
export const TASK_MANAGER_CREATE_ORGANIZATION_PAGE = "/web/task-manager/organizations/create";
export const TASK_MANAGER_ORGANIZATION_INDEX_PAGE = new Endpoint(RequestMethod.GET, "/web/task-manager/organizations/{organizationId}");

//Organization
export const TASK_MANAGER_CREATE_ORGANIZATION = new Endpoint(RequestMethod.POST, "/api/task-manager/organizations");
export const TASK_MANAGER_GET_ORGANIZATIONS = new Endpoint(RequestMethod.GET, "/api/task-manager/organizations");
export const TASK_MANAGER_GET_ORGANIZATION = new Endpoint(RequestMethod.GET, "/api/task-manager/organizations/{organizationId}");

//Invitation
export const TASK_MANAGER_GET_INVITATIONS = new Endpoint(RequestMethod.GET, "/api/task-manager/invitations");
export const TASK_MANAGER_ACCEPT_INVITATION = new Endpoint(RequestMethod.POST, "/api/task-manager/invitations/{organizationId}");
export const TASK_MANAGER_REJECT_INVITATION = new Endpoint(RequestMethod.DELETE, "/api/task-manager/invitations/{organizationId}");

//Notification
export const TASK_MANAGER_GET_NOTIFICATIONS = new Endpoint(RequestMethod.GET, "/api/task-manager/notifications/{organizationId}");
export const TASK_MANAGER_DELETE_NOTIFICATION = new Endpoint(RequestMethod.DELETE, "/api/task-manager/notifications");
export const TASK_MANAGER_SET_NOTIFICATION_STATUS = new Endpoint(RequestMethod.POST, "/api/task-manager/notifications/status");