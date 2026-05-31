package com.github.saphyra.apphub.service.feature.calendar.common.dao;

public class CalendarDaoConstants {
    public static final String COLUMN_PK = "pk";
    public static final String COLUMN_SK = "sk";
    public static final String COLUMN_REPETITION_TYPE = "repetition_type";
    public static final String COLUMN_REPETITION_DATA = "repetition_data";
    public static final String COLUMN_REPEAT_FOR_DAYS = "repeat_for_days";
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_END_DATE = "end_date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_REMIND_ME_BEFORE_DAYS = "remind_me_before_days";
    public static final String COLUMN_EXPIRATION_NOTIFIED = "expiration_notified";
    public static final String COLUMN_ARCHIVED = "archived";
    public static final String COLUMN_EVENT_IDS = "event_ids";
    public static final String COLUMN_LABEL_IDS = "label_ids";
    public static final String COLUMN_LABEL = "label";
    public static final String COLUMN_DATE_BUCKET = "date_bucket";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_REMINDED = "reminded";
    public static final String COLUMN_USER_ID = "user_id";

    public static final String PREFIX_USER = "USER#";
    public static final String PREFIX_EVENT = "EVENT#";
    public static final String PREFIX_LABEL_EVENT_MAPPING = "LABEL_EVENT_MAPPING#"; //Events of label
    public static final String PREFIX_EVENT_LABEL_MAPPING = "EVENT_LABEL_MAPPING#"; //Labels of event
    public static final String PREFIX_LABEL = "LABEL#";
    public static final String PREFIX_OCCURRENCE = "OCCURRENCE#";

    public static final String GSI_USER_ID_DATE_BUCKET = "GSI-user_id-date_bucket";
}
