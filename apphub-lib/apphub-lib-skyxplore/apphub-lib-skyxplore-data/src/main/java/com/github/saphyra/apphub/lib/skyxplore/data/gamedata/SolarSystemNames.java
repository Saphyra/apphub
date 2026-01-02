package com.github.saphyra.apphub.lib.skyxplore.data.gamedata;

import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.common_util.RomanNumberConverter;
import com.github.saphyra.apphub.lib.data.ClassPathList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
@Slf4j
public class SolarSystemNames extends ClassPathList<String> {
    private final RomanNumberConverter romanNumberConverter;
    private final Random random;

    public SolarSystemNames(ObjectMapper objectMapper, RomanNumberConverter romanNumberConverter, Random random) {
        super(objectMapper, "data/name/solar_system_names.json");
        this.romanNumberConverter = romanNumberConverter;
        this.random = random;
    }

    public String getRandomStarName(List<String> usedStarNames) {
        String nameBase = get(random.randInt(0, size() - 1));
        for (int i = 0; true; i++) {
            String name = i > 0 ? String.format("%s - %s", nameBase, romanNumberConverter.toRoman(i + 1)) : nameBase;

            if (!usedStarNames.contains(name)) {
                return name;
            }
        }
    }
}
