package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system;

import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.SolarSystemProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SolarSystemPlacerServiceTest {
    private static final Range<Integer> SOLAR_SYSTEM_DISTANCE_RANGE = new Range<>(100, 500);

    @Mock
    private GameProperties gameProperties;

    @Mock
    private Random random;

    @Mock
    private DistanceCalculator distanceCalculator;

    @Mock
    private SolarSystemShifter solarSystemShifter;

    @InjectMocks
    private SolarSystemPlacerService underTest;

    @Mock
    private SolarSystemProperties solarSystemProperties;

    @Test
    void place() {
        UUID[] solarSystem1 = {null};
        UUID[] solarSystem2 = {UUID.randomUUID()};

        List<UUID[]> solarSystems = new ArrayList<>();
        solarSystems.add(solarSystem1);
        solarSystems.add(solarSystem2);

        given(gameProperties.getSolarSystem()).willReturn(solarSystemProperties);
        given(solarSystemProperties.getSolarSystemDistance()).willReturn(SOLAR_SYSTEM_DISTANCE_RANGE);
        given(random.randInt(0, 0)).willReturn(0);
        given(random.randInt(SOLAR_SYSTEM_DISTANCE_RANGE)).willReturn(200);
        given(random.randBoolean()).willReturn(true);
        Coordinate generatedCoordinate = new Coordinate(200, 200);
        given(distanceCalculator.getDistance(generatedCoordinate, GameConstants.ORIGO)).willReturn(300d);

        given(solarSystemShifter.shiftSolarSystems(Map.of(GameConstants.ORIGO, solarSystem1, generatedCoordinate, solarSystem2))).willReturn(Map.of(GameConstants.ORIGO, solarSystem2));

        Map<Coordinate, UUID[]> result = underTest.place(solarSystems);

        assertThat(result).containsEntry(GameConstants.ORIGO, solarSystem2);
    }
}