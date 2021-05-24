package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system_connection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.Line;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionsOfConnectedSystemsCalculatorTest {
    private static final Integer NUMBER_OF_CONNECTIONS = 134;

    @Mock
    private ConnectionCounter connectionCounter;

    @InjectMocks
    private ConnectionsOfConnectedSystemsCalculator underTest;

    @Mock
    private Coordinate coordinate1;

    @Mock
    private Coordinate coordinate2;

    @Mock
    private Line line1;

    @Mock
    private Line line2;

    @Test
    public void getNumberOfConnectionsOfConnectedSystems() {
        given(line1.isEndpoint(coordinate1)).willReturn(false);
        given(line2.isEndpoint(coordinate1)).willReturn(true);
        given(line2.getOtherEndpoint(coordinate1)).willReturn(coordinate2);

        given(connectionCounter.getNumberOfConnections(coordinate2, Arrays.asList(line1, line2))).willReturn(NUMBER_OF_CONNECTIONS);

        Map<Coordinate, Integer> result = underTest.getNumberOfConnectionsOfConnectedSystems(coordinate1, Arrays.asList(line1, line2));

        assertThat(result).containsEntry(coordinate2, NUMBER_OF_CONNECTIONS);
    }
}