package com.github.saphyra.apphub.service.feature.elite_base.dao.item.type;

import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ItemTypeDto {
    private final String itemName;
    private final ObjectType type;
}
