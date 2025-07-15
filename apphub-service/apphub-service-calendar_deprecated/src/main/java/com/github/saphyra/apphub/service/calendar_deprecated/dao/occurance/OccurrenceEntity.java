package com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    private String eventId;
    private String userId;
    private String date;
    private String time;
    @Enumerated(EnumType.STRING)
    private OccurrenceStatus status;
    private String note;
    private String type;
}
