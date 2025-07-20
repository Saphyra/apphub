package com.github.saphyra.apphub.service.calendar.domain.event.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = "calendar", name = "event")
class EventEntity {
    @Id
    private String eventId;
    private String userId;
    private String repetitionType;
    private String repetitionData;
    private String repeatForDays;
    private String startDate;
    private String time;
    private String endDate;
    private String title;
    private String content;
    private String remindMeBeforeDays;
}
