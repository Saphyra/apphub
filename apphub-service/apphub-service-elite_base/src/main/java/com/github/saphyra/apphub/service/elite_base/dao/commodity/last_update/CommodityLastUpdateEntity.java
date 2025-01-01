package com.github.saphyra.apphub.service.elite_base.dao.commodity.last_update;

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
@Table(schema = "elite_base", name = "commodity_last_update")
class CommodityLastUpdateEntity {
    @Id
    private String externalReference;
    private String lastUpdate;
}
