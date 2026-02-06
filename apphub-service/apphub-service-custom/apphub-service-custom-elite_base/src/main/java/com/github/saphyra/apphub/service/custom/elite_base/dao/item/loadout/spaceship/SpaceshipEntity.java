package com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.spaceship;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemEntityId;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_ITEM_SPACESHIP;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(schema = SCHEMA, name = TABLE_ITEM_SPACESHIP)
class SpaceshipEntity {
    @EmbeddedId
    private ItemEntityId id;
    @Enumerated(EnumType.STRING)
    private ItemLocationType locationType;
    private Long marketId;
    private String starSystemId;
}
