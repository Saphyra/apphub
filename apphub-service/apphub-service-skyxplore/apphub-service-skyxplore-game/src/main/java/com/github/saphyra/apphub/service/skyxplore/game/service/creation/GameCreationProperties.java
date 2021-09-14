package com.github.saphyra.apphub.service.skyxplore.game.service.creation;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.AiPresence;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.PlanetSize;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemAmount;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemSize;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.UniverseSize;
import com.github.saphyra.apphub.lib.common_domain.Range;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "game.creation")
@Data
@Configuration
@Slf4j
public class GameCreationProperties {
    private SolarSystemProperties solarSystem;
    private PlanetProperties planet;
    private SurfaceProperties surface;
    private PlayerCreationProperties player;
    private CitizenProperties citizen;
    private SkillProperties skill;

    @PostConstruct
    public void after() {
        log.info("GameCreationProperties: {}", this);
    }

    @Data
    public static class SolarSystemProperties {
        private Map<UniverseSize, Double> solarSystemDistanceMultiplier;
        private Range<Integer> solarSystemDistance;
        private int minPlanetDistance;
        private Map<SystemAmount, Range<Double>> amountMultiplier;
        private Map<SystemSize, Range<Integer>> radius;
    }

    @Data
    public static class PlanetProperties {
        private Map<SystemSize, Range<Integer>> planetsPerSystem;
        private Map<PlanetSize, Range<Integer>> planetSize;
    }

    @Data
    public static class SurfaceProperties {
        private List<SurfaceTypeSpawnDetails> spawnDetails;
    }

    @Data
    public static class SurfaceTypeSpawnDetails {
        private String surfaceName;
        private Integer spawnRate;
        private boolean optional = false;
    }

    @Data
    public static class PlayerCreationProperties {
        private Map<AiPresence, Range<Double>> aiSpawnChance;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CitizenProperties {
        private int defaultMorale;
        private int defaultSatiety;
    }

    @Data
    public static class SkillProperties {
        private int initialNextLevel;
    }
}
