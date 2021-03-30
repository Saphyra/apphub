package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.home_planet.closest_system;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SystemConnection;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class WaypointCandidateFilterTest {
    @InjectMocks
    private WaypointCandidateFilter underTest;

    @Mock
    private SolarSystem solarSystem;

    @Mock
    private Universe universe;

    @Mock
    private Coordinate coordinate1;

    @Mock
    private Coordinate coordinate2;

    @Mock
    private SystemConnection connection1;

    @Mock
    private SystemConnection connection2;

    @Mock
    private SystemConnection connection3;

    @Mock
    private SystemConnection connection4;

    @Mock
    private Line line1;

    @Mock
    private Line line2;

    @Mock
    private Line line3;

    @Mock
    private Line line4;

    @Test
    public void getWaypointCandidates() {
        given(solarSystem.getCoordinate()).willReturn(coordinate1);

        given(connection1.getLine()).willReturn(line1);
        given(connection2.getLine()).willReturn(line2);
        given(connection3.getLine()).willReturn(line3);
        given(connection4.getLine()).willReturn(line4);

        given(line1.isEndpoint(coordinate1)).willReturn(true);
        given(line2.isEndpoint(coordinate1)).willReturn(true);
        given(line3.isEndpoint(coordinate1)).willReturn(true);
        given(line4.isEndpoint(coordinate1)).willReturn(false);

        given(line1.getA()).willReturn(coordinate1);
        given(line2.getB()).willReturn(coordinate1);

        given(universe.getConnections()).willReturn(Arrays.asList(connection1, connection2, connection3, connection4));

        List<Line> result = underTest.getWayPointCandidates(solarSystem, universe, Arrays.asList(coordinate1));

        assertThat(result).containsExactly(line3);
    }
}