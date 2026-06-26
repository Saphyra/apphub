package com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.fc_material;

import com.github.saphyra.apphub.service.feature.elite_base.dao.item.ItemLocationType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FcMaterialFactory {
    public FcMaterial create(ItemLocationType locationType, UUID externalReference, Long marketId, String name, Integer buyPrice, Integer sellPrice, Integer demand, Integer stock, UUID starSystemId) {
        return FcMaterial.builder()
            .locationType(locationType)
            .externalReference(externalReference)
            .marketId(marketId)
            .itemName(name)
            .buyPrice(buyPrice)
            .sellPrice(sellPrice)
            .demand(demand)
            .stock(stock)
            .starSystemId(starSystemId)
            .build();
    }
}
