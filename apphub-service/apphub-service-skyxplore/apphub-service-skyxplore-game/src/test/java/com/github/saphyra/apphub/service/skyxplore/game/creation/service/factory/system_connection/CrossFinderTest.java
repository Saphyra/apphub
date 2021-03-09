package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system_connection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.saphyra.apphub.lib.geometry.Cross;
import com.github.saphyra.apphub.lib.geometry.CrossCalculator;
import com.github.saphyra.apphub.lib.geometry.Line;

@RunWith(MockitoJUnitRunner.class)
public class CrossFinderTest {
    @Mock
    private CrossCalculator crossCalculator;

    @InjectMocks
    private CrossFinder underTest;

    @Mock
    private Line line1;

    @Mock
    private Line line2;

    @Mock
    private Cross cross;

    @Test
    public void findCross_found() {
        given(crossCalculator.getCrossPointOfSections(line1, line2, false)).willReturn(Optional.of(cross));

        Optional<Cross> result = underTest.findCross(Arrays.asList(line1, line2));

        assertThat(result).contains(cross);
    }

    @Test
    public void findCross_notFound() {
        Optional<Cross> result = underTest.findCross(Arrays.asList(line1, line2));

        assertThat(result).isEmpty();
    }
}