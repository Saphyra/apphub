package com.github.saphyra.apphub.lib.config.common.endpoints;

public class CalendarEndpoints {
    public static final String CALENDAR_PAGE = "/web/calendar";

    public static final String CALENDAR_GET_CALENDAR = "/api/calendar/calendar";
    public static final String CALENDAR_CREATE_EVENT = "/api/calendar/events";
    public static final String CALENDAR_OCCURRENCE_EDIT = "/api/calendar/occurrence/{occurrenceId}/edit";
    public static final String CALENDAR_OCCURRENCE_DONE = "/api/calendar/occurrence/{occurrenceId}/done";
    public static final String CALENDAR_OCCURRENCE_DEFAULT = "/api/calendar/occurrence/{occurrenceId}/default";
    public static final String CALENDAR_OCCURRENCE_SNOOZED = "/api/calendar/occurrence/{occurrenceId}/snoozed";
    public static final String CALENDAR_EVENT_DELETE = "/api/calendar/event/{eventId}";
    public static final String CALENDAR_SEARCH = "/api/calendar/search";
    public static final String CALENDAR_GET_EVENTS = "/api/calendar/events";
    public static final String CALENDAR_DELETE_EVENT = "/api/calendar/events/{eventId}";
    public static final String CALENDAR_EDIT_EVENT = "/api/calendar/events/{eventId}";
    public static final String CALENDAR_CREATE_LABEL = "/api/calendar/labels";
    public static final String CALENDAR_GET_LABELS = "/api/calendar/labels";
    public static final String CALENDAR_DELETE_LABEL = "/api/calendar/labels/{labelId}";
    public static final String CALENDAR_EDIT_LABEL = "/api/calendar/labels/{labelId}";
    public static final String CALENDAR_CREATE_OCCURRENCE = "/api/calendar/occurrences/{eventId}";
    public static final String CALENDAR_EDIT_OCCURRENCE = "/api/calendar/occurrences/{occurrenceId}";
    public static final String CALENDAR_GET_OCCURRENCES = "/api/calendar/occurrences";
}
