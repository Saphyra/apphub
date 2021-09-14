package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.universe;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class UniverseSizeCalculator {
    int calculateUniverseSize(List<SolarSystem> solarSystems) {
        double maxX = solarSystems.stream()
            .map(SolarSystem::getCoordinate)
            .map(CoordinateModel::getCoordinate)
            .mapToDouble(Coordinate::getX)
            .max()
            .orElseThrow(() -> new RuntimeException("No SolarSystems found"));
        double maxY = solarSystems.stream()
            .map(SolarSystem::getCoordinate)
            .map(CoordinateModel::getCoordinate)
            .mapToDouble(Coordinate::getY)
            .max()
            .orElseThrow(() -> new RuntimeException("No SolarSystems found"));

        return (int) Math.max(maxX, maxY);
    }
}
