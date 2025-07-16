package com.github.saphyra.apphub.api.calendar.model;

public enum OccurrenceStatus {
    PENDING, //Record contains user data, should not be moved
    DONE,
    SNOOZED, //Dismissed by user
    EXPIRED,
}
