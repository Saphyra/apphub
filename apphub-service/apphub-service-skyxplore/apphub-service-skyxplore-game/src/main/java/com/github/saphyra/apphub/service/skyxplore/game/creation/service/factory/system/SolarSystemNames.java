package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.common_util.RomanNumberConverter;
import com.github.saphyra.apphub.lib.data.ClassPathList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
class SolarSystemNames extends ClassPathList<String> {
    private final RomanNumberConverter romanNumberConverter;
    private final Random random;

    SolarSystemNames(ObjectMapperWrapper objectMapper, RomanNumberConverter romanNumberConverter, Random random) {
        super(objectMapper, "star_names.json");
        this.romanNumberConverter = romanNumberConverter;
        this.random = random;
    }

    String getRandomStarName(List<String> usedStarNames) {
        String nameBase = get(random.randInt(0, size() - 1));
        for (int i = 0; true; i++) {
            String name = i > 0 ? String.format("%s - %s", nameBase, romanNumberConverter.toRoman(i)) : nameBase;

            if (!usedStarNames.contains(name)) {
                return name;
            }
        }
    }
}
