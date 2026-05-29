package com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao;

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
@Deprecated(forRemoval = true)
class DeprecatedEventEntity {
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
    private String expirationNotified;
    private String archived;
}
