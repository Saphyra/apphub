package com.github.saphyra.apphub.service.elite_base.dao.body_data;

import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.service.elite_base.dao.body_data.body_material.BodyMaterial;
import com.github.saphyra.apphub.service.elite_base.dao.body_data.body_ring.BodyRing;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class BodyData {
    private final UUID bodyId;
    private LocalDateTime lastUpdate;
    private Boolean landable;
    private Double surfaceGravity;
    private ReserveLevel reserveLevel;
    private Boolean hasRing;
    private LazyLoadedField<List<BodyMaterial>> materials;
    private LazyLoadedField<List<BodyRing>> rings;

    public List<BodyMaterial> getMaterials() {
        return materials.get();
    }

    public List<BodyRing> getRings() {
        return rings.get();
    }
}
