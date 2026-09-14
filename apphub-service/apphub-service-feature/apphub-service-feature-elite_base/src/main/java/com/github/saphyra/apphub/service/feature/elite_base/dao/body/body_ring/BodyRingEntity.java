package com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_ring;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_BODY_RING;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = SCHEMA, name = TABLE_BODY_RING)
public class BodyRingEntity {
    @Id
    private String id;
    private String bodyId;
    private String name;
    @Enumerated(EnumType.STRING)
    private RingType type;
    private Double innerRadius;
    private Double outerRadius;
    private Double mass;
}
