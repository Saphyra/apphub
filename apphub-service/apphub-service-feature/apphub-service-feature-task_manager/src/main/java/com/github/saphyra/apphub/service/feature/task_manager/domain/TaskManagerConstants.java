package com.github.saphyra.apphub.service.feature.task_manager.domain;

public class TaskManagerConstants {
    public static final String COLUMN_ORGANIZATION = "organization";
    public static final String COLUMN_OPERATIONS = "operations";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_NOTIFICATION_TYPE = "notificationType";
    public static final String COLUMN_CREATED_AT = "createdAt";
    public static final String COLUMN_LAST_MODIFIED = "lastModified";
    public static final String COLUMN_DATA = "data";
    public static final String COLUMN_EXPIRATION = "expiration";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_INVITED_BY = "invitedBy";
    public static final String COLUMN_PRINCIPAL = "principal";
    public static final String COLUMN_OBJECT = "object";
    public static final String COLUMN_USER = "user";
    public static final String COLUMN_NOTIFICATION = "notification";

    public static final String PREFIX_USER = "USER#";
    public static final String PREFIX_ORGANIZATION = "ORGANIZATION#";
    public static final String PREFIX_NOTIFICATION = "NOTIFICATION#";

    public static final String GSI_ALM_OBJECT_PRINCIPAL = "GSI-alm-object-principal";
    public static final String GSI_INVITATION_INVITED_BY = "GSI-invitation-invited_by";
    public static final String GSI_INVITATION_ORGANIZATION = "GSI-invitation-organization";
    public static final String GSI_NOTIFICATION_ORGANIZATION_USER = "GSI-notification-organization-user";

    public static final int MAX_ORGANIZATION_NAME_LENGTH = 1024;
    public static final int MAX_ORGANIZATION_DESCRIPTION_LENGTH = 1024 * 100; // 100 KB;
}
