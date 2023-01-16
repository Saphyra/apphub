package com.github.saphyra.apphub.service.diary.dao.event;

import com.github.saphyra.apphub.api.diary.model.RepetitionType;
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
@Table(schema = "diary", name = "event")
class EventEntity {
    @Id
    private String eventId;
    private String userId;
    private String startDate;
    private String time;
    @Enumerated(EnumType.STRING)
    private RepetitionType repetitionType;
    private String repetitionData;
    private String title;
    private String content;
    private String repeat;
}
