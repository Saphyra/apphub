package com.github.saphyra.apphub.service.elite_base.dao.body;

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
@Table(schema = "elite_base", name = "body")
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
