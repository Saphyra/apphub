package com.github.saphyra.apphub.service.elite_base.dao.commodity.last_update;

import com.github.saphyra.apphub.service.elite_base.dao.commodity.CommodityType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Embeddable
public class CommodityLastUpdateId implements Serializable {
    private String externalReference;
    @Enumerated(EnumType.STRING)
    private CommodityType commodityType;
}
