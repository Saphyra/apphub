package com.github.saphyra.apphub.service.diary.dao.event;

import com.github.saphyra.apphub.api.diary.model.RepetitionType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class Event {
    private final UUID eventId;
    private final UUID userId;
    private final LocalDate startDate;
    private RepetitionType repetitionType;
    private String repetitionData;
    private String title;
    private String content;
}
