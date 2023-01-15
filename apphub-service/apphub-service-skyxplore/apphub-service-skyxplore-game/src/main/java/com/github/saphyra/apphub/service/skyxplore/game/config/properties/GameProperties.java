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
    private int eventQueueSize;
    private int itemSaverMaxChunkSize;
    private int creationQueueSize;
    private SolarSystemProperties solarSystem;
    private PlanetProperties planet;
    private SurfaceProperties surface;
    private PlayerProperties player;
    private CitizenProperties citizen;

    @PostConstruct
    public void after() {
        log.info("GameProperties: {}", this);
    }
}
