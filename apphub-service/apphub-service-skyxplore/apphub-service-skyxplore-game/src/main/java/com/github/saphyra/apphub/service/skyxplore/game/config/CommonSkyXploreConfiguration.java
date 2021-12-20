package com.github.saphyra.apphub.service.skyxplore.game.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Configuration
@Validated
@Data
public class CommonSkyXploreConfiguration {
    @Value("${abandonedGameExpirationSeconds}")
    @NotNull
    @Positive
    private Integer abandonedGameExpirationSeconds;
}
