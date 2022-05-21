package com.github.saphyra.apphub.service.diary.dao.event;

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
@Table(schema = "diary", name = "event")
class EventEntity {
    @Id
    private String eventId;
    private String userId;
    @Enumerated(EnumType.STRING)
    private RepetitionType repetitionType;
    private String repetitionData;
    private String title;
    private String content;
}
