package com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionOrderModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ProductionOrderConverter {
    public ProductionOrderModel toModel(UUID gameId, ProductionOrder productionOrder) {
        return ;
    }
}
