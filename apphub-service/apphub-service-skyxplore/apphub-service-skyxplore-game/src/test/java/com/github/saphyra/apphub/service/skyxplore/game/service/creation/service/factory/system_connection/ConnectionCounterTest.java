package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system_connection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.Line;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionCounterTest {
    @InjectMocks
    private ConnectionCounter underTest;

    @Mock
    private Line line1;

    @Mock
    private Line line2;

    @Mock
    private Coordinate coordinate;

    @Test
    public void getNumberOfConnections(){
        given(line1.isEndpoint(coordinate)).willReturn(true);

        assertThat(underTest.getNumberOfConnections(coordinate, Arrays.asList(line1, line2))).isEqualTo(1);
    }
}