package com.github.saphyra.apphub.integration.framework.endpoints;

public class TaskManagerEndpoints {
    //Pages
    public static final String TASK_MANAGER_PAGE = "/web/task-manager";
    public static final String TASK_MANAGER_CREATE_ORGANIZATION_PAGE = "/web/task-manager/organizations/create";
    public static final String TASK_MANAGER_ORGANIZATION_INDEX_PAGE = "/web/task-manager/organizations/{organizationId}";

    //Organization
    public static final String TASK_MANAGER_CREATE_ORGANIZATION = "/api/task-manager/organizations";
    public static final String TASK_MANAGER_GET_ORGANIZATIONS = "/api/task-manager/organizations";
    public static final String TASK_MANAGER_GET_ORGANIZATION = "/api/task-manager/organizations/{organizationId}";

    //Invitation
    public static final String TASK_MANAGER_GET_INVITATIONS = "/api/task-manager/invitations";
    public static final String TASK_MANAGER_ACCEPT_INVITATION = "/api/task-manager/invitations/{organizationId}";
    public static final String TASK_MANAGER_REJECT_INVITATION = "/api/task-manager/invitations/{organizationId}";

    //Notification
    public static final String TASK_MANAGER_GET_NOTIFICATIONS = "/api/task-manager/notifications/{organizationId}";
    public static final String TASK_MANAGER_SET_NOTIFICATION_STATUS = "/api/task-manager/notifications/status";
    public static final String TASK_MANAGER_DELETE_NOTIFICATION = "/api/task-manager/notifications";
}
