package com.github.saphyra.apphub.service.custom.elite_base.dao.loadout;

import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityLocation;
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

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(schema = "elite_base", name = "loadout")
@IdClass(LoadoutEntityId.class)
class LoadoutEntity {
    @Id
    private String externalReference;
    @Id
    @Enumerated(EnumType.STRING)
    private LoadoutType type;
    @Id
    private String name;
    @Enumerated(EnumType.STRING)
    private CommodityLocation commodityLocation;
    private Long marketId;
}
