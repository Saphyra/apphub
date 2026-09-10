package com.github.saphyra.apphub.service.feature.calendar.domain.event.dao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
class EventEntity {
    private String userId;
    private String eventId;
    private String repetitionType;
    private String repetitionData;
    private String repeatForDays;
    private String startDate;
    private String endDate;
    private String time;
    private String title;
    private String content;
    private String remindMeBeforeDays;
    private String expirationNotified;
    private String archived;
    private String autoDone;
}
