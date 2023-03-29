package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
//TODO split
public class SolarSystemGenerationService {
    private final Random random;
    private final SolarSystemPlacerService solarSystemPlacerService;
    private final PlanetPlacerService planetPlacerService;

    public List<NewbornSolarSystem> generateSolarSystems(Collection<Player> players, SkyXploreGameSettings settings) {
        List<UUID[]> solarSystems = new ArrayList<>();

        players.forEach(player -> assignPlayer(player, solarSystems, settings));

        Stream.generate(() -> newSolarSystem(settings, false))
            .limit(random.randInt(settings.getAdditionalSolarSystems()))
            .forEach(solarSystems::add);


        return solarSystemPlacerService.place(solarSystems)
            .entrySet()
            .stream()
            .map(entry -> planetPlacerService.placePlanets(entry.getKey(), entry.getValue()))
            .toList();
    }

    private void assignPlayer(Player player, List<UUID[]> solarSystems, SkyXploreGameSettings settings) {
        Optional<UUID[]> maybeSolarSystem = selectSolarSystem(solarSystems, settings);

        UUID[] solarSystem;
        if (maybeSolarSystem.isPresent()) {
            solarSystem = maybeSolarSystem.get();
        } else {
            solarSystem = newSolarSystem(settings, true);
            solarSystems.add(solarSystem);
        }

        solarSystem[selectEmptyPlanet(solarSystem)] = player.getUserId();
    }

    private Optional<UUID[]> selectSolarSystem(List<UUID[]> solarSystems, SkyXploreGameSettings settings) {
        List<UUID[]> possibilities = new ArrayList<>(getSuitableSolarSystems(solarSystems, settings));
        possibilities.add(null);

        return Optional.ofNullable(possibilities.get(random.randInt(0, possibilities.size() - 1)));
    }

    private List<UUID[]> getSuitableSolarSystems(List<UUID[]> solarSystems, SkyXploreGameSettings settings) {
        return solarSystems.stream()
            .filter(solarSystem -> hasSuitablePlanet(solarSystem, settings))
            .toList();
    }

    private boolean hasSuitablePlanet(UUID[] planet, SkyXploreGameSettings settings) {
        int maxNumberOfOccupiedPlanets = Math.min(planet.length, settings.getMaxPlayersPerSolarSystem());

        int occupiedPlanetsCount = (int) Arrays.stream(planet)
            .filter(userId -> !isNull(userId))
            .count();

        return occupiedPlanetsCount < maxNumberOfOccupiedPlanets;
    }

    private UUID[] newSolarSystem(SkyXploreGameSettings settings, boolean atLeastOnePlanet) {
        Range<Integer> planetsPerSolarSystem = settings.getPlanetsPerSolarSystem();
        int min = atLeastOnePlanet ? Math.max(1, planetsPerSolarSystem.getMin()) : planetsPerSolarSystem.getMin();

        int planetCount = random.randInt(min, planetsPerSolarSystem.getMax());

        return new UUID[planetCount];
    }

    private int selectEmptyPlanet(UUID[] solarSystem) {
        List<Integer> availablePlanets = Stream.iterate(0, integer -> integer + 1)
            .limit(solarSystem.length)
            .filter(integer -> isNull(solarSystem[integer]))
            .toList();

        return availablePlanets.get(random.randInt(0, availablePlanets.size() - 1));
    }
}
