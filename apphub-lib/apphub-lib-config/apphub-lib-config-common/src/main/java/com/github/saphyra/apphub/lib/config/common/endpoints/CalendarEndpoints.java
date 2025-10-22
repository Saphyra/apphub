package com.github.saphyra.apphub.lib.config.common.endpoints;

public class CalendarEndpoints {
    public static final String CALENDAR_PAGE = "/web/calendar";

    public static final String CALENDAR_CREATE_EVENT = "/api/calendar/events";
    public static final String CALENDAR_GET_EVENTS = "/api/calendar/events";
    public static final String CALENDAR_DELETE_EVENT = "/api/calendar/events/{eventId}";
    public static final String CALENDAR_EDIT_EVENT = "/api/calendar/events/{eventId}";
    public static final String CALENDAR_CREATE_LABEL = "/api/calendar/labels";
    public static final String CALENDAR_GET_LABELS = "/api/calendar/labels";
    public static final String CALENDAR_GET_LABEL = "/api/calendar/labels/{labelId}";
    public static final String CALENDAR_DELETE_LABEL = "/api/calendar/labels/{labelId}";
    public static final String CALENDAR_EDIT_LABEL = "/api/calendar/labels/{labelId}";
    public static final String CALENDAR_CREATE_OCCURRENCE = "/api/calendar/occurrences/{eventId}";
    public static final String CALENDAR_EDIT_OCCURRENCE = "/api/calendar/occurrences/{occurrenceId}";
    public static final String CALENDAR_GET_OCCURRENCES = "/api/calendar/occurrences";
    public static final String CALENDAR_GET_EVENT = "/api/calendar/events/{eventId}";
    public static final String CALENDAR_GET_OCCURRENCES_OF_EVENT = "/api/calendar/events/{eventId}/occurrences";
    public static final String CALENDAR_GET_OCCURRENCE = "/api/calendar/occurrences/{occurrenceId}";
    public static final String CALENDAR_EDIT_OCCURRENCE_STATUS = "/api/calendar/occurrences/{occurrenceId}/status";
    public static final String CALENDAR_DELETE_OCCURRENCE = "/api/calendar/occurrences/{occurrenceId}";
    public static final String CALENDAR_OCCURRENCE_REMINDED = "/api/calendar/occurrences/{occurrenceId}/reminded";
    public static final String CALENDAR_GET_LABELS_OF_EVENT = "/api/calendar/events/{eventId}/labels";
}
