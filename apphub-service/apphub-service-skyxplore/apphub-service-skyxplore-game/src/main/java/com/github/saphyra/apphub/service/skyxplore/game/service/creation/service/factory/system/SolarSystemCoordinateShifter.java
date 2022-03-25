package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class SolarSystemCoordinateShifter {
    private final GameProperties gameCreationProperties;

    void shiftCoordinates(List<SolarSystem> solarSystems) {
        double minX = solarSystems.stream()
            .mapToDouble(value -> value.getCoordinate().getX())
            .min()
            .orElseThrow(() -> new RuntimeException("No SolarSystems found"));
        double minY = solarSystems.stream()
            .mapToDouble(value -> value.getCoordinate().getY())
            .min()
            .orElseThrow(() -> new RuntimeException("No SolarSystems found"));

        Integer minDistance = gameCreationProperties.getSolarSystem()
            .getSolarSystemDistance()
            .getMin();
        double diffX = -1 * minX + minDistance;
        double diffY = -1 * minY + minDistance;

        solarSystems.forEach(solarSystem -> {
            CoordinateModel model = solarSystem.getCoordinate();
            Coordinate newCoordinate = new Coordinate(model.getX() + diffX, model.getY() + diffY);
            model.setCoordinate(newCoordinate);
        });
    }
}
