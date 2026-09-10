package com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.power;

import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.Power;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM_POWER_MAPPING_V2;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = SCHEMA, name = TABLE_STAR_SYSTEM_POWER_MAPPING_V2)
@IdClass(StarSystemPowerMappingEntity.class)
class StarSystemPowerMappingEntity implements Serializable {
    @Id
    private String starSystemId;
    @Id
    @Enumerated(EnumType.STRING)
    private Power power;
}
