package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system_connection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.saphyra.apphub.lib.geometry.Cross;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.lib.geometry.Line;

@RunWith(MockitoJUnitRunner.class)
public class CrossRemovalServiceTest {
    @Mock
    private DistanceCalculator distanceCalculator;

    @Mock
    private CrossFinder crossFinder;

    @InjectMocks
    private CrossRemovalService underTest;

    @Mock
    private Line line1;

    @Mock
    private Line line2;

    @Mock
    private Cross cross;

    @Test
    public void removeCrosses() {
        given(crossFinder.findCross(Arrays.asList(line1, line2))).willReturn(Optional.of(cross));
        given(cross.getLine1()).willReturn(line1);
        given(cross.getLine2()).willReturn(line2);
        given(line1.getLength(distanceCalculator)).willReturn(10d);
        given(line2.getLength(distanceCalculator)).willReturn(9d);

        List<Line> result = underTest.removeCrosses(Arrays.asList(line1, line2));

        assertThat(result).containsExactly(line2);
    }
}