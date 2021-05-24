package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemAmount;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.GameCreationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
class MaxSystemCountCalculator {
    public static final List<SystemAmount> NOT_RANDOM_SYSTEM_AMOUNTS = Arrays.stream(SystemAmount.values())
        .filter(systemAmount1 -> systemAmount1 != SystemAmount.RANDOM)
        .collect(Collectors.toList());

    private final GameCreationProperties properties;
    private final Random random;

    int getMaxAllocationTryCount(int universeSize, SystemAmount systemAmount) {

        SystemAmount amount = systemAmount == SystemAmount.RANDOM ? NOT_RANDOM_SYSTEM_AMOUNTS.get(random.randInt(0, NOT_RANDOM_SYSTEM_AMOUNTS.size() - 1)) : systemAmount;

        Double sizeMultiplier = properties.getSolarSystem()
            .getSizeMultiplier()
            .get(amount);
        return (int) (universeSize * sizeMultiplier);
    }
}
