package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.player;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.AiPresence;
import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class AiCountCalculator {
    private final GameProperties gameCreationProperties;
    private final Random random;

    /**
     * @param playerCount   How many non-ai player wants to play the game
     * @param aiPresence    User setting, what determinates how many additional ai should be generated
     * @param allianceCount The number of teams the non-ai players are grouped to
     * @return The number of the to-be-generated ai-s. Minimum 1, if the allianceCount is 1
     */
    int getAiCount(double playerCount, AiPresence aiPresence, int allianceCount) {
        Range<Double> range = gameCreationProperties.getPlayer()
            .getAiSpawnChance()
            .get(aiPresence);
        int expectedAiCount = (int) Math.round(random.randDouble(playerCount * range.getMin(), playerCount * range.getMax()));

        return allianceCount == 1 ? Math.max(1, expectedAiCount) : expectedAiCount;
    }
}
