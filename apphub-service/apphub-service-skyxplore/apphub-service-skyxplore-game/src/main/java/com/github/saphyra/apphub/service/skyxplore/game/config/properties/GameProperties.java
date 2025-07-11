package com.github.saphyra.apphub.service.skyxplore.game.config.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@ConfigurationProperties(prefix = "game")
@Data
@Configuration
@Slf4j
public class GameProperties {
    private int itemSaverMaxChunkSize;
    private int creationQueueSize;
    private int tickTimeMillis;
    private int pauseGameAfterDisconnectionSeconds;
    private int logisticsWeightMultiplier;
    private SolarSystemProperties solarSystem;
    private PlanetProperties planet;
    private SurfaceProperties surface;
    private CitizenProperties citizen;
    private DeconstructionProperties deconstruction;
    private MessageDelay messageDelay;
    private ProductionProperties production;

    @PostConstruct
    public void after() {
        log.info("GameProperties: {}", this);

        surface.validate();
    }
}
