package com.github.saphyra.apphub.service.feature.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.api.feature.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.service.feature.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ProductionOrderProcessConditions {
    boolean productionNeeded(GameData gameData, UUID productionOrderId) {
        return !gameData.getProductionOrders()
            .findByIdValidated(productionOrderId)
            .allStarted();
    }

    public boolean isFinished(GameData gameData, UUID processId, UUID productionOrderId) {
        return !productionNeeded(gameData, productionOrderId) && gameData.getProcesses().getByExternalReference(processId).stream().allMatch(process -> process.getStatus() == ProcessStatus.DONE);
    }
}
