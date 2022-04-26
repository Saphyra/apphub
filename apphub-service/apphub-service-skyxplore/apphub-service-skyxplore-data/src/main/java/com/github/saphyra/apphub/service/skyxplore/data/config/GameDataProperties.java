package com.github.saphyra.apphub.service.skyxplore.data.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class GameDataProperties {
    @Value("${game.deletion.expirationMinutes}")
    private Integer gameDeletionExpirationMinutes;
}
