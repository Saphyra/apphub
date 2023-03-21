package com.github.saphyra.apphub.service.calendar.dao.occurance;

public enum OccurrenceStatus {
    VIRTUAL, //Record created to track repetitions, but can be deleted anytime automatically
    PENDING, //Record contains user data, should not be moved
    DONE,
    SNOOZED, //Dismissed by user
    EXPIRED,
}
