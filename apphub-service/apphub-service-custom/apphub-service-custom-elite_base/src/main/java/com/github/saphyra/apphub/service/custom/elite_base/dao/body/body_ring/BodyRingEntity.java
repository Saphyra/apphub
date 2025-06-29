package com.github.saphyra.apphub.service.custom.elite_base.dao.body.body_ring;

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
@Table(schema = "elite_base", name = "body_ring")
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
