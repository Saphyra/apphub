package com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_data;

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
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_BODY_DATA;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = SCHEMA, name = TABLE_BODY_DATA)
 class BodyDataEntity {
    @Id
    private String bodyId;
    private String lastUpdate;
    private Boolean landable;
    private Double surfaceGravity;
    @Enumerated(EnumType.STRING)
    private com.github.saphyra.apphub.api.feature.elite_base.model.ReserveLevel reserveLevel;
    private Boolean hasRing;
}
