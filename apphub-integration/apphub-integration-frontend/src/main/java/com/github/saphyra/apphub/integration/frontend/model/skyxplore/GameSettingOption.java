package com.github.saphyra.apphub.integration.frontend.model.skyxplore;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

import static com.github.saphyra.apphub.integration.frontend.model.skyxplore.GameSettingOptionValue.COMMON;
import static com.github.saphyra.apphub.integration.frontend.model.skyxplore.GameSettingOptionValue.EVERYWHERE;
import static com.github.saphyra.apphub.integration.frontend.model.skyxplore.GameSettingOptionValue.LARGE;
import static com.github.saphyra.apphub.integration.frontend.model.skyxplore.GameSettingOptionValue.MEDIUM;
import static com.github.saphyra.apphub.integration.frontend.model.skyxplore.GameSettingOptionValue.NONE;
import static com.github.saphyra.apphub.integration.frontend.model.skyxplore.GameSettingOptionValue.RANDOM;
import static com.github.saphyra.apphub.integration.frontend.model.skyxplore.GameSettingOptionValue.RARE;
import static com.github.saphyra.apphub.integration.frontend.model.skyxplore.GameSettingOptionValue.SMALL;
import static com.github.saphyra.apphub.integration.frontend.model.skyxplore.GameSettingOptionValue.SMALLEST;

@AllArgsConstructor
@Getter
public enum GameSettingOption {
    UNIVERSE_SIZE("universe-size-input", Arrays.asList(SMALLEST, SMALL, MEDIUM, LARGE)),
    SYSTEM_AMOUNT("system-amount-input", Arrays.asList(SMALL, MEDIUM, COMMON, RANDOM)),
    SYSTEM_SIZE("system-size-input", Arrays.asList(SMALL, MEDIUM, COMMON, RANDOM)),
    PLANET_SIZE("planet-size-input", Arrays.asList(SMALL, MEDIUM, COMMON, RANDOM)),
    AI_PRESENCE("ai-presence-input", Arrays.asList(NONE, RARE, COMMON, EVERYWHERE));

    private final String inputFieldId;
    private final List<GameSettingOptionValue> possibleOptions;
}