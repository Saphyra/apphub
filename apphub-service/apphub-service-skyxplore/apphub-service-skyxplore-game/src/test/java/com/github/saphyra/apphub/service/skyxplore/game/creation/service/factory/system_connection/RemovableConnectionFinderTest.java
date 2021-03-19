package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system_connection;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.lib.geometry.Line;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class RemovableConnectionFinderTest {
    @Mock
    private DistanceCalculator distanceCalculator;

    @Mock
    private ConnectionsOfConnectedSystemsCalculator connectionsOfConnectedSystemsCalculator;

    @InjectMocks
    private RemovableConnectionFinder underTest;

    @Mock
    private Coordinate coordinate1;

    @Mock
    private Coordinate coordinate2;

    @Mock
    private Coordinate coordinate3;

    @Mock
    private Coordinate coordinate4;

    @Mock
    private Line line1;

    @Mock
    private Line line2;

    @Mock
    private Line line3;

    @Test
    public void getRemovableLine() {
        given(connectionsOfConnectedSystemsCalculator.getNumberOfConnectionsOfConnectedSystems(coordinate1, Arrays.asList(line1, line2, line3))).willReturn(CollectionUtils.toMap(
            new BiWrapper<>(coordinate2, 1),
            new BiWrapper<>(coordinate3, 2),
            new BiWrapper<>(coordinate4, 2)
        ));
        given(line2.isEndpoint(coordinate1)).willReturn(true);
        given(line2.isEndpoint(coordinate3)).willReturn(true);
        given(line3.isEndpoint(coordinate1)).willReturn(true);
        given(line3.isEndpoint(coordinate4)).willReturn(true);

        given(line3.getLength(distanceCalculator)).willReturn(3d);
        given(line2.getLength(distanceCalculator)).willReturn(2d);

        Line result = underTest.getRemovableLine(coordinate1, Arrays.asList(line1, line2, line3));

        assertThat(result).isEqualTo(line3);
    }
}