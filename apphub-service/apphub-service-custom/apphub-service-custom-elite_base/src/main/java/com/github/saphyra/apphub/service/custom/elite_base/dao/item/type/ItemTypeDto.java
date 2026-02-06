package com.github.saphyra.apphub.service.custom.elite_base.dao.item.type;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ItemTypeDto {
    private final String itemName;
    private final ItemType type;
}
