package com.github.saphyra.apphub.service.feature.elite_base.dao.body;

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
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_BODY;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = SCHEMA, name = TABLE_BODY)
class BodyEntity {
    @Id
    private String id;
    private String lastUpdate;
    private String starSystemId;
    @Enumerated(EnumType.STRING)
    private BodyType type;
    private Long bodyId;
    private String bodyName;
    private Double distanceFromStar;
}
