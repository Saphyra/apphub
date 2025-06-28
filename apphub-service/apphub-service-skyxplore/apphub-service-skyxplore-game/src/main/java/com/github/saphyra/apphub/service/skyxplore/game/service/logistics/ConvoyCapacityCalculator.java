package com.github.saphyra.apphub.service.skyxplore.game.service.logistics;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConvoyCapacityCalculator {
    private static final int MAX_CONVOY_CAPACITY = 20;

    public int calculate(GameData gameData, UUID location, int requestedCapacity) {
        return Math.min(requestedCapacity, MAX_CONVOY_CAPACITY);
    }
}
