package com.github.saphyra.apphub.service.custom.elite_base.dao.body_data;

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
@Table(schema = "elite_base", name = "body_data")
 class BodyDataEntity {
    @Id
    private String bodyId;
    private String lastUpdate;
    private Boolean landable;
    private Double surfaceGravity;
    @Enumerated(EnumType.STRING)
    private ReserveLevel reserveLevel;
    private Boolean hasRing;
}
