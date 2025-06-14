package com.github.saphyra.apphub.service.skyxplore.game.domain.data.convoy;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.service.logistics.ConvoyCapacityCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.service.logistics.RouteCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ConvoyFactory {
    private final IdGenerator idGenerator;
    private final ConvoyConverter convoyConverter;
    private final ConvoyCapacityCalculator convoyCapacityCalculator;
    private final RouteCalculator routeCalculator;

    public Convoy save(GameProgressDiff progressDiff, GameData gameData, UUID location, UUID resourceDeliveryRequestId, int requestedCapacity) {
        int capacity = convoyCapacityCalculator.calculate(gameData, location, requestedCapacity);
        Convoy convoy = create(resourceDeliveryRequestId, capacity);

        routeCalculator.calculateForResourceDeliveryRequestId(progressDiff, gameData, location, convoy.getConvoyId(), resourceDeliveryRequestId);

        progressDiff.save(convoyConverter.toModel(gameData.getGameId(), convoy));
        gameData.getConvoys()
            .add(convoy);

        log.info("Saved: {}", convoy);

        return convoy;
    }

    private Convoy create(UUID resourceDeliveryRequestId, int capacity) {
        return Convoy.builder()
            .convoyId(idGenerator.randomUuid())
            .resourceDeliveryRequestId(resourceDeliveryRequestId)
            .capacity(capacity)
            .build();
    }
}
