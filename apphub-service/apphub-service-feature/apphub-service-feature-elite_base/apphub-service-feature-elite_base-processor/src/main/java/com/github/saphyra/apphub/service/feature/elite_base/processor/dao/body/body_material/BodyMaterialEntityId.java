package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.body.body_material;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Embeddable
class BodyMaterialEntityId {
    private String bodyId;
    private String material;
}
