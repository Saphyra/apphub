package com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.structure.Range;
import com.github.saphyra.apphub.integration.structure.skyxplore.SkyXploreGameSettings;

public class SkyXploreLobbySettingsHelper {
    public static SkyXploreGameSettings withMaxPlayersPerSolarSystem(int maxPlayersPerSolarSystem) {
        return createDefault()
            .toBuilder()
            .maxPlayersPerSolarSystem(maxPlayersPerSolarSystem)
            .build();
    }

    public static SkyXploreGameSettings createDefault() {
        return SkyXploreGameSettings.builder()
            .maxPlayersPerSolarSystem(2)
            .additionalSolarSystems(new Range<>(1, 2))
            .planetsPerSolarSystem(new Range<>(0, 3))
            .planetSize(new Range<>(10, 15))
            .build();
    }

    public static SkyXploreGameSettings withAdditionalSolarSystems(Range<Integer> value) {
        return createDefault()
            .toBuilder()
            .additionalSolarSystems(value)
            .build();
    }

    public static SkyXploreGameSettings withPlanetsPerSolarSystem(Range<Integer> value) {
        return createDefault()
            .toBuilder()
            .planetsPerSolarSystem(value)
            .build();
    }

    public static SkyXploreGameSettings withPlanetSize(Range<Integer> value) {
        return createDefault()
            .toBuilder()
            .planetSize(value)
            .build();
    }
}
