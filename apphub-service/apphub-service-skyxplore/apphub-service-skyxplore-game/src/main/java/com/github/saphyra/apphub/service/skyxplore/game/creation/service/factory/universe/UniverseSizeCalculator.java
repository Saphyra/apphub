package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.universe;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.UniverseSize;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.service.skyxplore.game.creation.GameCreationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class UniverseSizeCalculator {
    private final GameCreationProperties properties;
    private final Random random;

    int calculate(int memberNum, UniverseSize universeSize) {
        GameCreationProperties.UniverseProperties universeProperties = properties.getUniverse();
        int baseSize = universeProperties.getBaseSize();
        double memberMultiplication = universeProperties.getMemberMultiplication();
        double settingMultiplication = universeProperties.getSettingMultiplication().get(universeSize);

        return (int) Math.ceil(baseSize * memberNum * memberMultiplication * settingMultiplication * random.randDouble(universeProperties.getMinMultiplier(), universeProperties.getMaxMultiplier()));
    }
}
