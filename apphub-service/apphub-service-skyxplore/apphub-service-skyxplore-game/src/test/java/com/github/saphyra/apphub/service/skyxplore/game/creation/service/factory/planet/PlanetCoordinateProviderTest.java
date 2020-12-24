package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.planet;

import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(MockitoJUnitRunner.class)
@Slf4j
public class PlanetCoordinateProviderTest {
    private static final Coordinate STAR = new Coordinate(0, 0);

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private Random random;

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private DistanceCalculator distanceCalculator;

    @InjectMocks
    private PlanetCoordinateProvider underTest;

    @Test
    public void generate() {
        List<Double> distances = Stream.generate(() -> underTest.getCoordinate(100))
            .limit(1000)
            .map(coordinate -> distanceCalculator.getDistance(coordinate, STAR))
            .collect(Collectors.toList());
        distances.stream()
            .sorted(Comparator.comparingDouble(o -> o))
            .forEach(distance -> log.info("{}", distance));

        double average = distances.stream()
            .mapToDouble(value -> value)
            .average()
            .getAsDouble();
        log.info("Average: {}", average);
    }
}