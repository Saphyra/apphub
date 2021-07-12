package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.home_planet.closest_system;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.game.common.LineModelWrapper;
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
    private CoordinateModel coordinateModel;

    @Mock
    private Coordinate coordinate;

    @Mock
    private SystemConnection connection1;

    @Mock
    private SystemConnection connection2;

    @Mock
    private SystemConnection connection3;

    @Mock
    private SystemConnection connection4;

    @Mock
    private LineModelWrapper lineModelWrapper1;

    @Mock
    private LineModelWrapper lineModelWrapper2;

    @Mock
    private LineModelWrapper lineModelWrapper3;

    @Mock
    private LineModelWrapper lineModelWrapper4;

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
        given(solarSystem.getCoordinate()).willReturn(coordinateModel);
        given(coordinateModel.getCoordinate()).willReturn(coordinate);

        given(connection1.getLine()).willReturn(lineModelWrapper1);
        given(connection2.getLine()).willReturn(lineModelWrapper2);
        given(connection3.getLine()).willReturn(lineModelWrapper3);
        given(connection4.getLine()).willReturn(lineModelWrapper4);

        given(lineModelWrapper1.getLine()).willReturn(line1);
        given(lineModelWrapper2.getLine()).willReturn(line2);
        given(lineModelWrapper3.getLine()).willReturn(line3);
        given(lineModelWrapper4.getLine()).willReturn(line4);

        given(line1.isEndpoint(coordinate)).willReturn(true);
        given(line2.isEndpoint(coordinate)).willReturn(true);
        given(line3.isEndpoint(coordinate)).willReturn(true);
        given(line4.isEndpoint(coordinate)).willReturn(false);

        given(line1.getA()).willReturn(coordinate);
        given(line2.getB()).willReturn(coordinate);

        given(universe.getConnections()).willReturn(Arrays.asList(connection1, connection2, connection3, connection4));

        List<Line> result = underTest.getWayPointCandidates(solarSystem, universe, Arrays.asList(coordinate));

        assertThat(result).containsExactly(line3);
    }
}