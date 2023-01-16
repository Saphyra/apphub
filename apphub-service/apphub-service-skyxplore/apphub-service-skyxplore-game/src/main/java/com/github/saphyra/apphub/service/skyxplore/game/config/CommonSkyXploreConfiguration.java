package com.github.saphyra.apphub.service.skyxplore.game.config;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@Validated
@Data
public class CommonSkyXploreConfiguration {
    @Value("${abandonedGameExpirationSeconds}")
    @NotNull
    @Positive
    private Integer abandonedGameExpirationSeconds;
}
