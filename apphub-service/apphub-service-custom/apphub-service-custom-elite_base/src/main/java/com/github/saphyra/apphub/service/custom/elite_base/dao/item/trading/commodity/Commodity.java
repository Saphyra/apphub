package com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.commodity;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.Tradeable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
public class Commodity implements Tradeable {
    private UUID externalReference; //CarrierId/StationId
    private final String itemName;
    private ItemLocationType locationType;
    private Long marketId;
    private Integer buyPrice;
    private Integer sellPrice;
    private Integer demand;
    private Integer stock;
    private final UUID starSystemId;

    @Override
    public ItemType getItemType() {
        return ItemType.COMMODITY;
    }
}
