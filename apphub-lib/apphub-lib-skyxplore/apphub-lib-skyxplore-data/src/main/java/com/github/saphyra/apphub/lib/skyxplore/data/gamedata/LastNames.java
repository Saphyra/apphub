package com.github.saphyra.apphub.lib.skyxplore.data.gamedata;

import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.data.ClassPathList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
public class LastNames extends ClassPathList<String> {
    private final Random random;

    public LastNames(ObjectMapper objectMapper, Random random) {
        super(objectMapper, "data/name/last_names.json");
        this.random = random;
    }

    public String getRandom() {
        return get(random.randInt(0, size() - 1));
    }
}
