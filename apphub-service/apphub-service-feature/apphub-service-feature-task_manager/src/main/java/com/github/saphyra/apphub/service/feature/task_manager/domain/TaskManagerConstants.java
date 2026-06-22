package com.github.saphyra.apphub.service.feature.task_manager.domain;

public class TaskManagerConstants {
    public static final String COLUMN_PK = "pk";
    public static final String COLUMN_SK = "sk";
    public static final String COLUMN_OPERATIONS = "operations";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_NOTIFICATION_TYPE = "notificationType";
    public static final String COLUMN_CREATED_AT = "createdAt";
    public static final String COLUMN_LAST_MODIFIED = "lastModified";
    public static final String COLUMN_DATA = "data";
    public static final String COLUMN_EXPIRATION = "expiration";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";

    public static final String PREFIX_USER = "USER#";
    public static final String PREFIX_ORGANIZATION = "ORGANIZATION#";
    public static final String PREFIX_NOTIFICATION = "NOTIFICATION#";

    public static final int MAX_ORGANIZATION_NAME_LENGTH = 1024;
    public static final int MAX_ORGANIZATION_DESCRIPTION_LENGTH = 1024 * 100; // 100 KB;
}
