package com.github.saphyra.apphub.service.custom.elite_base.dao.body.body_material;

import jakarta.persistence.Entity;
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
@Table(schema = "elite_base", name = "body_material")
class BodyMaterialEntity {
    @Id
    private String id;
    private String bodyId;
    private String material;
    private Double percent;
}
