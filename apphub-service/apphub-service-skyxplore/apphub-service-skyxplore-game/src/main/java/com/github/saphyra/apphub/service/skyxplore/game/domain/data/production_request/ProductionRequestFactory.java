package com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_request;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductionRequestFactory {
    private final IdGenerator idGenerator;
    private final ProductionRequestConverter productionRequestConverter;

    public ProductionRequest save(GameProgressDiff progressDiff, GameData gameData, UUID reservedStorageId, int requestedAmount) {
        ProductionRequest request = ProductionRequest.builder()
            .productionRequestId(idGenerator.randomUuid())
            .reservedStorageId(reservedStorageId)
            .requestedAmount(requestedAmount)
            .dispatchedAmount(0)
            .build();

        progressDiff.save(productionRequestConverter.toModel(gameData.getGameId(), request));
        gameData.getProductionRequests()
            .add(request);

        return request;
    }
}
