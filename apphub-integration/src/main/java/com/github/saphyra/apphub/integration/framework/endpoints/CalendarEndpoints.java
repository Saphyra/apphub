package com.github.saphyra.apphub.integration.framework.endpoints;

public class CalendarEndpoints {
    public static final String CALENDAR_PAGE = "/web/calendar";

    public static final String CALENDAR_GET_CALENDAR = "/api/calendar/calendar";
    public static final String CALENDAR_CREATE_EVENT = "/api/calendar/event";
    public static final String CALENDAR_OCCURRENCE_EDIT = "/api/calendar/occurrence/{occurrenceId}/edit";
    public static final String CALENDAR_OCCURRENCE_DONE = "/api/calendar/occurrence/{occurrenceId}/done";
    public static final String CALENDAR_OCCURRENCE_DEFAULT = "/api/calendar/occurrence/{occurrenceId}/default";
    public static final String CALENDAR_OCCURRENCE_SNOOZED = "/api/calendar/occurrence/{occurrenceId}/snoozed";
    public static final String CALENDAR_EVENT_DELETE = "/api/calendar/event/{eventId}";
    public static final String CALENDAR_SEARCH = "/api/calendar/search";
}
