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
    private String repetitionType; //Encrypted
    private String repetitionData; //Encrypted
    private String repeatForDays; //Encrypted
    private String startDate; //Encrypted
    private String endDate; //Encrypted
    private String time; //Encrypted
    private String title; //Encrypted
    private String content; //Encrypted
    private String remindMeBeforeDays; //Encrypted
    private String expirationNotified; //Encrypted
    private String archived; //Encrypted
}
