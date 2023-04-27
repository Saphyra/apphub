package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.FirstNames;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.LastNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
public class RandomNameProvider {
    private final FirstNames firstNames;
    private final LastNames lastNames;

    public String getRandomName(Collection<String> exclusions) {
        String result;

        do {
            log.debug("Generating name, where exclusions are {}", exclusions);
            result = String.join(" ", lastNames.getRandom(), firstNames.getRandom());
        } while (exclusions.contains(result));

        return result;
    }
}
