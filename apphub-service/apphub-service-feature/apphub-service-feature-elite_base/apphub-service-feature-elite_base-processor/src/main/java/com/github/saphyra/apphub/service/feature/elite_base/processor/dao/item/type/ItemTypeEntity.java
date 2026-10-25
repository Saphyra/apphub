package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.type;

import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.ObjectType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.TABLE_ITEM_TYPE;

@Entity
@Table(schema = SCHEMA, name = TABLE_ITEM_TYPE)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
class ItemTypeEntity {
    @Id
    private String itemName;
    @Enumerated(EnumType.STRING)
    private ObjectType type;
}
