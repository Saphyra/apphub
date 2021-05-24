package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system_connection;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.Line;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TriangleConnectionFinderTest {
    @InjectMocks
    private TriangleConnectionFinder underTest;

    @Mock
    private Coordinate coordinate1;

    @Mock
    private Coordinate coordinate2;

    @Mock
    private Coordinate coordinate3;

    @Mock
    private Coordinate coordinate4;

    @Test
    public void triangle() {
        Line line1 = new Line(coordinate2, coordinate3);
        Line line2 = new Line(coordinate1, coordinate3);
        Line line3 = new Line(coordinate1, coordinate2);

        boolean result = underTest.isTriangle(coordinate1, line1, Arrays.asList(line1, line2, line3));

        assertThat(result).isTrue();
    }

    @Test
    public void notTriangle() {
        Line line1 = new Line(coordinate2, coordinate3);
        Line line2 = new Line(coordinate1, coordinate3);
        Line line3 = new Line(coordinate1, coordinate4);

        boolean result = underTest.isTriangle(coordinate1, line1, Arrays.asList(line1, line2, line3));

        assertThat(result).isFalse();
    }
}