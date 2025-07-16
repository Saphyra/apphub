package com.github.saphyra.apphub.service.calendar.domain.label.dao;

import jakarta.persistence.Entity;
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
@Table(schema = "calendar", name = "label")
class LabelEntity {
    private String labelId;
    private String userId;
    private String label;
}
