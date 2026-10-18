package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.star_system;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.TABLE_STAR_SYSTEM;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = SCHEMA, name = TABLE_STAR_SYSTEM)
class StarSystemEntity {
    @Id
    private String id;
    private Long starId;
    private String starName;
    private Double xPos;
    private Double yPos;
    private Double zPos;
    @Enumerated(EnumType.STRING)
    private StarType starType;
}
