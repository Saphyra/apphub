package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.loadout.equipment;

import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.ItemEntityId;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.ItemLocationType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.TABLE_ITEM_EQUIPMENT;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(schema = SCHEMA, name = TABLE_ITEM_EQUIPMENT)
class EquipmentEntity {
    @EmbeddedId
    private ItemEntityId id;
    @Enumerated(EnumType.STRING)
    private ItemLocationType locationType;
    private Long marketId;
    private String starSystemId;
}
