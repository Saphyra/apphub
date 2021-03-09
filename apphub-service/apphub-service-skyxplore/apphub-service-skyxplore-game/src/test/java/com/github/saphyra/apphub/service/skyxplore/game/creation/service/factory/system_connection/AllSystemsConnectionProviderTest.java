package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system_connection;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.Line;

@RunWith(MockitoJUnitRunner.class)
public class AllSystemsConnectionProviderTest {
    @InjectMocks
    private AllSystemsConnectionProvider underTest;

    @Mock
    private Coordinate coordinate1;

    @Mock
    private Coordinate coordinate2;

    @Test
    public void connectToAllSystems() {
        List<Line> result = underTest.connectToAllSystems(coordinate1, Arrays.asList(coordinate1, coordinate2));

        assertThat(result).containsExactly(new Line(coordinate1, coordinate2));
    }
}