import { Endpoint, RequestMethod } from "../dao";

export const CALENDAR_PAGE = "/web/calendar";
export const CALENDAR_LABELS_PAGE = "/web/calendar/labels";
export const CALENDAR_CREATE_EVENT_PAGE = new Endpoint(RequestMethod.GET, "/web/calendar/create-event");
export const CALENDAR_EDIT_EVENT_PAGE = new Endpoint(RequestMethod.GET, "/web/calendar/edit-event/{eventId}");
export const CALENDAR_EDIT_OCCURRENCE_PAGE = new Endpoint(RequestMethod.GET, "/web/calendar/edit-occurrence/{occurrenceId}");

export const CALENDAR_GET_LABELS = new Endpoint(RequestMethod.GET, "/api/calendar/labels");
export const CALENDAR_CREATE_LABEL = new Endpoint(RequestMethod.PUT, "/api/calendar/labels");
export const CALENDAR_GET_OCCURRENCES = new Endpoint(RequestMethod.GET, "/api/calendar/occurrences");
export const CALENDAR_CREATE_EVENT = new Endpoint(RequestMethod.PUT, "/api/calendar/events");
export const CALENDAR_DELETE_LABEL = new Endpoint(RequestMethod.DELETE, "/api/calendar/labels/{labelId}");
export const CALENDAR_EDIT_LABEL = new Endpoint(RequestMethod.POST, "/api/calendar/labels/{labelId}");
export const CALENDAR_GET_EVENTS = new Endpoint(RequestMethod.GET, "/api/calendar/events");
export const CALENDAR_GET_LABEL = new Endpoint(RequestMethod.GET, "/api/calendar/labels/{labelId}");
export const CALENDAR_GET_EVENT = new Endpoint(RequestMethod.GET, "/api/calendar/events/{eventId}");
export const CALENDAR_GET_OCCURRENCES_OF_EVENT = new Endpoint(RequestMethod.GET, "/api/calendar/events/{eventId}/occurrences");
export const CALENDAR_GET_OCCURRENCE = new Endpoint(RequestMethod.GET, "/api/calendar/occurrences/{occurrenceId}");
export const CALENDAR_EDIT_OCCURRENCE_STATUS = new Endpoint(RequestMethod.POST, "/api/calendar/occurrences/{occurrenceId}/status");
export const CALENDAR_DELETE_EVENT = new Endpoint(RequestMethod.DELETE, "/api/calendar/events/{eventId}");
export const CALENDAR_EDIT_OCCURRENCE = new Endpoint(RequestMethod.POST, "/api/calendar/occurrences/{occurrenceId}");