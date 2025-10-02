package com.github.saphyra.apphub.integration.structure.api.calendar;

import lombok.NonNull;

import java.util.Arrays;
import java.util.Optional;

public enum OccurrenceStatus {
    PENDING,
    DONE,
    SNOOZED, //Dismissed by user
    EXPIRED,
    REMINDER,
    ;

    public static Optional<OccurrenceStatus> parse(@NonNull String value) {
        return Arrays.stream(values())
            .filter(occurrenceStatus -> occurrenceStatus.name().equalsIgnoreCase(value))
            .findAny();
    }
}
