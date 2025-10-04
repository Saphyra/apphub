package com.github.saphyra.apphub.service.calendar.migration;

import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
class DeprecatedEvent {
    private final UUID eventId;
    private final UUID userId;
    private final LocalDate startDate; //Encrypted
    private final LocalTime time; //Encrypted
    private RepetitionType repetitionType;
    private String repetitionData; //Encrypted
    private String title; //Encrypted
    private String content; //Encrypted
    private int repeat; //Encrypted
}
