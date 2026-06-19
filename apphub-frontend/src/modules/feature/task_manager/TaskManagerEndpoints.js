import Endpoint from "common/js/dao/Endpoint";
import RequestMethod from "common/js/dao/RequestMethod";

export const TASK_MANAGER_PAGE = "/web/task-manager";
export const TASK_MANAGER_CREATE_ORGANIZATION_PAGE = "/web/task-manager/organizations/create";

export const TASK_MANAGER_CREATE_ORGANIZATION = new Endpoint(RequestMethod.POST, "/api/task-manager/organizations");
export const TASK_MANAGER_GET_ORGANIZATIONS = new Endpoint(RequestMethod.GET, "/api/task-manager/organizations");
export const TASK_MANAGER_GET_INVITATIONS = new Endpoint(RequestMethod.GET, "/api/task-manager/invitations");
export const TASK_MANAGER_ACCEPT_INVITATION = new Endpoint(RequestMethod.POST, "/api/task-manager/invitations/{organizationId}");
export const TASK_MANAGER_REJECT_INVITATION = new Endpoint(RequestMethod.DELETE, "/api/task-manager/invitations/{organizationId}");