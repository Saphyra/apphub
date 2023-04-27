package com.github.saphyra.apphub.service.skyxplore.lobby.service.creation;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConfigurationProperties(prefix = "lobby.creation")
@Data
public class GameSettingsProperties {
    private SkyXploreGameSettings settings;
    private Integer maxAiCount;

    SkyXploreGameSettings createDefaultSettings() {
        return settings.toBuilder()
            .build();
    }
}
