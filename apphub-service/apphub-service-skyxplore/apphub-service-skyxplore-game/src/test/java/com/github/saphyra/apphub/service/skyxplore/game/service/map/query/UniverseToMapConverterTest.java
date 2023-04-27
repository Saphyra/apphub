package com.github.saphyra.apphub.service.skyxplore.game.service.map.query;

import com.github.saphyra.apphub.api.skyxplore.response.game.map.MapResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.map.MapSolarSystemResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UniverseToMapConverterTest {
    private static final int UNIVERSE_SIZE = 123;
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private SolarSystemResponseExtractor solarSystemResponseExtractor;

    @InjectMocks
    private UniverseToMapConverter underTest;

    @Mock
    private GameData gameData;

    @Mock
    private MapSolarSystemResponse solarSystemResponse;

    @Test
    public void convert() {
        given(solarSystemResponseExtractor.getSolarSystems(USER_ID, gameData)).willReturn(Arrays.asList(solarSystemResponse));
        given(gameData.getUniverseSize()).willReturn(UNIVERSE_SIZE);

        MapResponse result = underTest.convert(USER_ID, gameData);

        assertThat(result.getUniverseSize()).isEqualTo(UNIVERSE_SIZE);
        assertThat(result.getSolarSystems()).containsExactly(solarSystemResponse);
    }
}