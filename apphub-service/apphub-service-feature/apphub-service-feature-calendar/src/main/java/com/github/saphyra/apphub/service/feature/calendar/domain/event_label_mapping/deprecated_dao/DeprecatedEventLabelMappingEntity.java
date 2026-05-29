package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.deprecated_dao;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
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
@Table(schema = "calendar", name = "event_label_mapping")
@IdClass(DeprecatedEventLabelMappingEntity.class)
@Deprecated(forRemoval = true)
class DeprecatedEventLabelMappingEntity {
    @Id
    private String eventId;
    @Id
    private String labelId;
    @Id
    private String userId;
}
