package com.github.saphyra.apphub.service.feature.calendar.common.dao;

import com.github.saphyra.apphub.lib.dynamodb.MonitoringFunctionality;

public enum CalendarMonitoringFunctionality implements MonitoringFunctionality {
    //Common
    DELETE_ALL_BY_USER_ID_QUERY,
    DELETE_ALL_BY_USER_ID_DELETE,

    //Event
    FIND_EVENT_BY_ID,
    GET_EVENTS_BY_USER_ID,
    GET_EVENTS_BY_IDS,
    SAVE_EVENT,
    DELETE_EVENTS,

    //Occurrence
    SAVE_OCCURRENCE,
    GET_OCCURRENCES_BY_EVENT_ID,
    DELETE_OCCURRENCES,
    SAVE_OCCURRENCES,
    FIND_OCCURRENCE_BY_ID,
    GET_OCCURRENCE_BY_BUCKET,

    //Label
    GET_LABELS_BY_IDS,
    SAVE_LABEL,
    FIND_LABEL_BY_ID,
    GET_LABELS_BY_USER_ID,
    DELETE_LABEL,

    //EventLabelMapping
    GET_EVENTS_OF_LABEL,
    GET_LABELS_OF_EVENTS,
    GET_LABELS_OF_EVENTS_BY_USER_ID,
    SAVE_LABELS_OF_EVENT,
    GET_EVENTS_OF_LABELS,
    SAVE_EVENTS_OF_LABELS,
    DELETE_LABELS_OF_EVENTS,
    DELETE_EVENTS_OF_LABEL,
    GET_EVENTS_OF_LABELS_BY_USER_ID,
    SAVE_EVENTS_OF_LABEL,
    ;

    @Override
    public String assemble(String tableName) {
        return tableName + "_" + this.name();
    }
}
