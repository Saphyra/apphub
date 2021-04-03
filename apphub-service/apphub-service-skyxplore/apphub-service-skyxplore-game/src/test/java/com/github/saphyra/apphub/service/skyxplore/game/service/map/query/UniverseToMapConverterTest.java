package com.github.saphyra.apphub.service.skyxplore.game.service.map.query;

import com.github.saphyra.apphub.api.skyxplore.response.game.map.MapResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.map.MapSolarSystemResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.map.SolarSystemConnectionResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class UniverseToMapConverterTest {
    private static final int UNIVERSE_SIZE = 123;
    @Mock
    private SolarSystemResponseExtractor solarSystemResponseExtractor;

    @Mock
    private SolarSystemConnectionResponseExtractor solarSystemConnectionResponseExtractor;

    @InjectMocks
    private UniverseToMapConverter underTest;

    @Mock
    private Universe universe;

    @Mock
    private MapSolarSystemResponse solarSystemResponse;

    @Mock
    private SolarSystemConnectionResponse connectionResponse;

    @Test
    public void convert() {
        given(solarSystemResponseExtractor.getSolarSystems(universe)).willReturn(Arrays.asList(solarSystemResponse));
        given(solarSystemConnectionResponseExtractor.getConnections(universe)).willReturn(Arrays.asList(connectionResponse));
        given(universe.getSize()).willReturn(UNIVERSE_SIZE);

        MapResponse result = underTest.convert(universe);

        assertThat(result.getUniverseSize()).isEqualTo(UNIVERSE_SIZE);
        assertThat(result.getConnections()).containsExactly(connectionResponse);
        assertThat(result.getSolarSystems()).containsExactly(solarSystemResponse);
    }
}