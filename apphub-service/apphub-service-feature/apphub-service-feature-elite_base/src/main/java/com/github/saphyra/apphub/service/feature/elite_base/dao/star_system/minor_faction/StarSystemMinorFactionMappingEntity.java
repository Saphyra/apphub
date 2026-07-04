package com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.minor_faction;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM_MINOR_FACTION_MAPPING_V2;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = SCHEMA, name = TABLE_STAR_SYSTEM_MINOR_FACTION_MAPPING_V2)
@IdClass(StarSystemMinorFactionMappingEntity.class)
public class StarSystemMinorFactionMappingEntity implements Serializable {
    @Id
    private String starSystemId;

    @Id
    private String minorFactionId;
}
