package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system_connection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.game.creation.GameCreationProperties;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionOverflowRemovalServiceTest {
    private static final Integer MAX_NUMBER_OF_CONNECTIONS = 123;

    @Mock
    private GameCreationProperties properties;

    @Mock
    private ConnectionCounter connectionCounter;

    @Mock
    private RemovableConnectionFinder removableConnectionFinder;

    @InjectMocks
    private ConnectionOverflowRemovalService underTest;

    @Mock
    private Coordinate coordinate;

    @Mock
    private Line line1;

    @Mock
    private Line line2;

    @Mock
    private GameCreationProperties.SystemConnectionProperties systemConnectionProperties;

    @Test
    public void removeConnectionOverflow() {
        given(properties.getSystemConnection()).willReturn(systemConnectionProperties);
        given(systemConnectionProperties.getMaxNumberOfConnections()).willReturn(MAX_NUMBER_OF_CONNECTIONS);
        given(connectionCounter.getNumberOfConnections(coordinate, Arrays.asList(line1, line2))).willReturn(MAX_NUMBER_OF_CONNECTIONS + 1);
        given(connectionCounter.getNumberOfConnections(coordinate, Arrays.asList(line1))).willReturn(MAX_NUMBER_OF_CONNECTIONS - 1);

        given(removableConnectionFinder.getRemovableLine(coordinate, Arrays.asList(line1, line2))).willReturn(line2);

        List<Line> result = underTest.removeConnectionOverflow(Arrays.asList(coordinate), Arrays.asList(line1, line2));

        assertThat(result).containsExactly(line1);
    }
}