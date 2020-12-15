package com.github.saphyra.apphub.service.skyxplore.game.creation;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.AiPresence;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.PlanetSize;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemAmount;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemSize;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.UniverseSize;
import com.github.saphyra.apphub.lib.common_domain.Range;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "game.creation")
@Data
@Configuration
public class GameCreationProperties {
    private UniverseProperties universe;
    private SolarSystemProperties solarSystem;
    private PlanetProperties planet;
    private SurfaceProperties surface;
    private SystemConnectionProperties systemConnection;
    private PlayerCreationProperties player;

    @Data
    public static class UniverseProperties {
        private int baseSize;
        private Double memberMultiplication;
        private Map<UniverseSize, Double> settingMultiplication;
        private double minMultiplier;
        private double maxMultiplier;
    }

    @Data
    public static class SolarSystemProperties {
        private Map<SystemAmount, Double> sizeMultipliers;
        private int minSolarSystemDistance;
    }

    @Data
    public static class PlanetProperties {
        private Map<SystemSize, Range<Integer>> systemSize;
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
    public static class SystemConnectionProperties {
        private int maxDistance;
        private int maxNumberOfConnections;
    }

    @Data
    public static class PlayerCreationProperties {
        private Map<AiPresence, Integer> spawnChance;
    }
}
