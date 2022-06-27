package com.github.saphyra.apphub.service.diary.dao.occurance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = "diary", name = "occurrence")
class OccurrenceEntity {
    @Id
    private String occurrenceId;
    private String eventId;
    private String userId;
    private String date;
    @Enumerated(EnumType.STRING)
    private OccurrenceStatus status;
    private String note;
}
