package com.github.saphyra.apphub.service.calendar.domain.occurrence.dao;

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
@Table(schema = "calendar", name = "occurrence")
class OccurrenceEntity {
    @Id
    private String occurrenceId;
    private String userId;
    private String eventId;
    private String date;
    private String time;
    private String status;
    private String note;
    private String remindAt;
    private String reminded;
}
