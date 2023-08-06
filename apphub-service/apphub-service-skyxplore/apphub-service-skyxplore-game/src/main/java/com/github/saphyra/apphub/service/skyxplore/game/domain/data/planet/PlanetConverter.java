package com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlanetModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlanetConverter {
    public List<PlanetModel> toModel(UUID gameId, Collection<Planet> planets) {
        return planets.stream()
            .map(planet -> toModel(gameId, planet))
            .collect(Collectors.toList());
    }

    public PlanetModel toModel(UUID gameId, Planet planet) {
        PlanetModel model = new PlanetModel();
        model.setId(planet.getPlanetId());
        model.setGameId(gameId);
        model.setType(GameItemType.PLANET);
        model.setSolarSystemId(planet.getSolarSystemId());
        model.setDefaultName(planet.getDefaultName());
        model.setCustomNames(planet.getCustomNames());
        model.setOrbitRadius(planet.getOrbitRadius());
        model.setOrbitSpeed(planet.getOrbitSpeed());
        model.setSize(planet.getSize());
        model.setOwner(planet.getOwner());

        return model;
    }
}
