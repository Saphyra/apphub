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
public class TooShortConnectionRemovalServiceTest {
    @Mock
    private DistanceCalculator distanceCalculator;

    @Mock
    private TriangleConnectionFinder triangleConnectionFinder;

    @InjectMocks
    private TooShortConnectionRemovalService underTest;

    @Mock
    private Coordinate coordinate1;

    @Mock
    private Coordinate coordinate2;

    @Mock
    private Coordinate coordinate3;

    @Mock
    private Line line;

    @Test
    public void notEndpoint() {
        given(line.isEndpoint(coordinate1)).willReturn(true);

        List<Line> result = underTest.filterLinesTooCloseToASystem(Arrays.asList(coordinate1), Arrays.asList(line));

        assertThat(result).containsExactly(line);
    }

    @Test
    public void notTriangle() {
        given(line.isEndpoint(coordinate1)).willReturn(false);
        given(triangleConnectionFinder.isTriangle(coordinate1, line, Arrays.asList(line))).willReturn(false);

        List<Line> result = underTest.filterLinesTooCloseToASystem(Arrays.asList(coordinate1), Arrays.asList(line));

        assertThat(result).containsExactly(line);
    }

    @Test
    public void notTooClose() {
        given(line.isEndpoint(coordinate1)).willReturn(false);
        given(triangleConnectionFinder.isTriangle(coordinate1, line, Arrays.asList(line))).willReturn(true);

        given(line.getA()).willReturn(coordinate2);
        given(line.getB()).willReturn(coordinate3);

        given(distanceCalculator.getDistance(coordinate1, line)).willReturn(50d);
        given(distanceCalculator.getDistance(coordinate1, coordinate2)).willReturn(50d);
        given(distanceCalculator.getDistance(coordinate1, coordinate3)).willReturn(50d);

        List<Line> result = underTest.filterLinesTooCloseToASystem(Arrays.asList(coordinate1), Arrays.asList(line));

        assertThat(result).containsExactly(line);
    }

    @Test
    public void tooClose() {
        given(line.isEndpoint(coordinate1)).willReturn(false);
        given(triangleConnectionFinder.isTriangle(coordinate1, line, Arrays.asList(line))).willReturn(true);

        given(line.getA()).willReturn(coordinate2);
        given(line.getB()).willReturn(coordinate3);

        given(distanceCalculator.getDistance(coordinate1, line)).willReturn(1d);
        given(distanceCalculator.getDistance(coordinate1, coordinate2)).willReturn(50d);
        given(distanceCalculator.getDistance(coordinate1, coordinate3)).willReturn(50d);

        List<Line> result = underTest.filterLinesTooCloseToASystem(Arrays.asList(coordinate1), Arrays.asList(line));

        assertThat(result).isEmpty();
    }
}