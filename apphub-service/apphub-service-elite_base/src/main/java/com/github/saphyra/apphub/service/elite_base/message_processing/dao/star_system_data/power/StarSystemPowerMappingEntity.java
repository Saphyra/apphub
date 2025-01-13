package com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.power;

import com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.Power;
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

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = "elite_base", name = "star_system_power_mapping")
@IdClass(StarSystemPowerMappingEntity.class)
class StarSystemPowerMappingEntity implements Serializable {
    @Id
    private String starSystemId;
    @Id
    @Enumerated(EnumType.STRING)
    private Power power;
}
