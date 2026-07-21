package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder(toBuilder = true)
class OccurrenceEntity {
    private String userId;
    private String eventId;
    private String occurrenceId;
    private String dateBucket;
    private String date;
    private String time;
    private String status;
    private String note;
    private String remindMeBeforeDays;
    private String reminded;
    private String autoDone;
}
