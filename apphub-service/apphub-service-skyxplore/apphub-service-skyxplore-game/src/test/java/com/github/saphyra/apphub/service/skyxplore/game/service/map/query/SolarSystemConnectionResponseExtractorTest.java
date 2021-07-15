package com.github.saphyra.apphub.service.skyxplore.game.service.map.query;

import com.github.saphyra.apphub.api.skyxplore.response.game.map.SolarSystemConnectionResponse;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.game.common.LineModelWrapper;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SystemConnection;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
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
public class SolarSystemConnectionResponseExtractorTest {
    @InjectMocks
    private SolarSystemConnectionResponseExtractor underTest;

    @Mock
    private Universe universe;

    @Mock
    private Coordinate coordinate1;

    @Mock
    private Coordinate coordinate2;

    @Mock
    private LineModelWrapper lineModelWrapper;

    @Test
    public void getConnections() {
        SystemConnection connection = SystemConnection.builder()
            .line(lineModelWrapper)
            .build();
        given(universe.getConnections()).willReturn(Arrays.asList(connection));
        given(lineModelWrapper.getLine()).willReturn(new Line(coordinate1, coordinate2));

        List<SolarSystemConnectionResponse> result = underTest.getConnections(universe);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getA()).isEqualTo(coordinate1);
        assertThat(result.get(0).getB()).isEqualTo(coordinate2);
    }
}