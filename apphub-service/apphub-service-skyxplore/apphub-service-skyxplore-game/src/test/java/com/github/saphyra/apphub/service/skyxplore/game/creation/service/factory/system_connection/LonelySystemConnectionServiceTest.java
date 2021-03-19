package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system_connection;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.lib.geometry.Line;
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
public class LonelySystemConnectionServiceTest {
    @Mock
    private ConnectionCounter connectionCounter;

    @Mock
    private DistanceCalculator distanceCalculator;

    @InjectMocks
    private LonelySystemConnectionService underTest;

    @Mock
    private Coordinate coordinate1;

    @Mock
    private Coordinate coordinate2;

    @Mock
    private Coordinate coordinate3;

    @Test
    public void connectLonelySystems() {
        Line existingConnection = new Line(coordinate1, coordinate2);
        Line expectedConnection = new Line(coordinate3, coordinate2);

        given(connectionCounter.getNumberOfConnections(coordinate1, Arrays.asList(existingConnection))).willReturn(1);
        given(connectionCounter.getNumberOfConnections(coordinate2, Arrays.asList(existingConnection))).willReturn(1);
        given(connectionCounter.getNumberOfConnections(coordinate3, Arrays.asList(existingConnection))).willReturn(0);
        given(connectionCounter.getNumberOfConnections(coordinate1, Arrays.asList(existingConnection, expectedConnection))).willReturn(1);
        given(connectionCounter.getNumberOfConnections(coordinate2, Arrays.asList(existingConnection, expectedConnection))).willReturn(1);
        given(connectionCounter.getNumberOfConnections(coordinate3, Arrays.asList(existingConnection, expectedConnection))).willReturn(1);

        given(distanceCalculator.getDistance(coordinate3, coordinate1)).willReturn(100d);
        given(distanceCalculator.getDistance(coordinate3, coordinate2)).willReturn(50d);

        List<Line> result = underTest.connectLonelySystems(Arrays.asList(coordinate1, coordinate2, coordinate3), Arrays.asList(existingConnection));

        assertThat(result).containsExactlyInAnyOrder(existingConnection, expectedConnection);
    }
}