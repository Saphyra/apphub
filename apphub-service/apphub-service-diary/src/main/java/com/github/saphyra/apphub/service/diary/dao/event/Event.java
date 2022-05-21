package com.github.saphyra.apphub.service.diary.dao.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class Event {
    private final UUID eventId;
    private final UUID userId;
    private RepetitionType repetitionType;
    private Map<String, String> repetitionData;
    private String title;
    private String content;
}
