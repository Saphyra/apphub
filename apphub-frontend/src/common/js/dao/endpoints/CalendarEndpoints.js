import { Endpoint, RequestMethod } from "../dao";

export const CALENDAR_PAGE = "/web/calendar";
export const CALENDAR_LABELS_PAGE = "/web/calendar/labels";
export const CALENDAR_CREATE_EVENT_PAGE = new Endpoint(RequestMethod.GET, "/web/calendar/create-event");

export const CALENDAR_GET_LABELS = new Endpoint(RequestMethod.GET, "/api/calendar/labels");
export const CALENDAR_GET_OCCURRENCES = new Endpoint(RequestMethod.GET, "/api/calendar/occurrences");