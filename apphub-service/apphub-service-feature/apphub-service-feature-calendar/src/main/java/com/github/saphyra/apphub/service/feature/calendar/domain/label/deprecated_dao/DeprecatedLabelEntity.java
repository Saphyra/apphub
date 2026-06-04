package com.github.saphyra.apphub.service.feature.calendar.domain.label.deprecated_dao;

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
@Table(schema = "calendar", name = "label")
@Deprecated(forRemoval = true)
class DeprecatedLabelEntity {
    @Id
    private String labelId;
    private String userId;
    private String label;
}
