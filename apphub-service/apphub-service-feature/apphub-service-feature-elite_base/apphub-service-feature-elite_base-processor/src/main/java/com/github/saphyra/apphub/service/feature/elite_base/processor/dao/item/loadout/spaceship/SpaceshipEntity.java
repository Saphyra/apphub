package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.loadout.spaceship;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.TABLE_ITEM_SPACESHIP_V2;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(schema = SCHEMA, name = TABLE_ITEM_SPACESHIP_V2)
class SpaceshipEntity {
    @Id
    private String id;
    private String itemName;
    private String stationId;
    private String fleetCarrierId;
    private Long marketId;
    private String starSystemId;
}
