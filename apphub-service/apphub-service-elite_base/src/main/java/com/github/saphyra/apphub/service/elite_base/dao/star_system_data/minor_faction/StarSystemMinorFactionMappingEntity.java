package com.github.saphyra.apphub.service.elite_base.dao.star_system_data.minor_faction;

import jakarta.persistence.Entity;
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
@Table(schema = "elite_base", name = "star_system_minor_faction_mapping")
@IdClass(StarSystemMinorFactionMappingEntity.class)
public class StarSystemMinorFactionMappingEntity implements Serializable {
    @Id
    private String starSystemId;

    @Id
    private String minorFactionId;
}
