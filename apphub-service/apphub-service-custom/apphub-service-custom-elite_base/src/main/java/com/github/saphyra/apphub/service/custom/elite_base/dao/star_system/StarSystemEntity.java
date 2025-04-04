package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system;

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
@Table(schema = "elite_base", name = "star_system")
class StarSystemEntity {
    @Id
    private String id;
    private String lastUpdate;
    private Long starId;
    private String starName;
    private Double xPos;
    private Double yPos;
    private Double zPos;
    @Enumerated(EnumType.STRING)
    private StarType starType;
}
