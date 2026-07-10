package com.github.saphyra.apphub.api.feature.calendar.model;

public class CalendarEndpoints {
    public static final String CALENDAR_CREATE_EVENT = "/api/calendar/events";
    public static final String CALENDAR_GET_EVENTS = "/api/calendar/events";
    public static final String CALENDAR_GET_LABELLESS_EVENTS = "/api/calendar/events/labelless";
    public static final String CALENDAR_DELETE_EVENT = "/api/calendar/events/{eventId}";
    public static final String CALENDAR_EDIT_EVENT = "/api/calendar/events/{eventId}";
    public static final String CALENDAR_CREATE_LABEL = "/api/calendar/labels";
    public static final String CALENDAR_GET_LABELS = "/api/calendar/labels";
    public static final String CALENDAR_GET_LABEL = "/api/calendar/labels/{labelId}";
    public static final String CALENDAR_DELETE_LABEL = "/api/calendar/labels/{labelId}";
    public static final String CALENDAR_EDIT_LABEL = "/api/calendar/labels/{labelId}";
    public static final String CALENDAR_CREATE_OCCURRENCE = "/api/calendar/occurrences/{eventId}";
    public static final String CALENDAR_EDIT_OCCURRENCE = "/api/calendar/{eventId}/occurrences/{occurrenceId}";
    public static final String CALENDAR_GET_OCCURRENCES = "/api/calendar/occurrences";
    public static final String CALENDAR_GET_EVENT = "/api/calendar/events/{eventId}";
    public static final String CALENDAR_GET_OCCURRENCES_OF_EVENT = "/api/calendar/events/{eventId}/occurrences";
    public static final String CALENDAR_GET_OCCURRENCE = "/api/calendar/{eventId}/occurrences/{occurrenceId}";
    public static final String CALENDAR_EDIT_OCCURRENCE_STATUS = "/api/calendar/{eventId}/occurrences/{occurrenceId}/status";
    public static final String CALENDAR_DELETE_OCCURRENCE = "/api/calendar/{eventId}/occurrences/{occurrenceId}";
    public static final String CALENDAR_OCCURRENCE_REMINDED = "/api/calendar/{eventId}/occurrences/{occurrenceId}/reminded";
    public static final String CALENDAR_GET_LABELS_OF_EVENT = "/api/calendar/events/{eventId}/labels";
    public static final String CALENDAR_GET_EXPIRED_EVENTS = "/api/calendar/events/expired";
    public static final String CALENDAR_HIDE_EXPIRED_EVENT = "/api/calendar/events/{eventId}/hide";
    public static final String CALENDAR_EXTEND_EXPIRED_EVENT = "/api/calendar/events/{eventId}/extend";
    public static final String CALENDAR_MERGE_EVENTS = "/api/calendar/events/{eventId}/merge";
    public static final String CALENDAR_SEARCH_EVENTS = "/api/calendar/events/search";
    public static final String CALENDAR_ARCHIVE_EVENT = "/api/calendar/events/{eventId}/archive";
    public static final String CALENDAR_GET_SHARED_ITEM = "/api/calendar/shared/{type}/{id}";
    public static final String CALENDAR_GET_OPERATIONS = "/api/calendar/shared/{type}/operations";
    public static final String CALENDAR_SHARE_OBJECT = "/api/calendar/shared";
    public static final String CALENDAR_SHARE_EDIT_OPERATIONS = "/api/calendar/shared/{type}/{id}/operations/{sharedWith}";
    public static final String CALENDAR_UNSHARE = "/api/calendar/shared/{type}/{id}/operations/{sharedWith}";
}
