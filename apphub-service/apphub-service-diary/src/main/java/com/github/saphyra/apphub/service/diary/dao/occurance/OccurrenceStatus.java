package com.github.saphyra.apphub.service.diary.dao.occurance;

public enum OccurrenceStatus {
    VIRTUAL, //Record created to track repetitions, but not saved to database
    PENDING, //Record is saved to database, waiting for actions
    DONE,
    SNOOZED, //Dismissed by user
    EXPIRED,
}
