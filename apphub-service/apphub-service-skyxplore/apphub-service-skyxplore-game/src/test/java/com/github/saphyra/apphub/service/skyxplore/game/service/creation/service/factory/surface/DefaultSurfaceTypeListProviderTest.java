package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.surface;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.SurfaceProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.SurfaceTypeSpawnDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class DefaultSurfaceTypeListProviderTest {
    @Mock
    private GameProperties properties;

    @Mock
    private SurfaceProperties surfaceProperties;

    @Mock
    private SurfaceTypeSpawnDetails spawnDetails;

    @Test
    public void generateSurfaceTypeList() {
        given(properties.getSurface()).willReturn(surfaceProperties);
        given(surfaceProperties.getSpawnDetails()).willReturn(Arrays.asList(spawnDetails));

        given(spawnDetails.getSurfaceName()).willReturn(SurfaceType.DESERT.name());
        given(spawnDetails.getSpawnRate()).willReturn(2);

        DefaultSurfaceTypeListProvider underTest = new DefaultSurfaceTypeListProvider(properties);

        assertThat(underTest.getSurfaceTypes()).containsExactly(SurfaceType.DESERT, SurfaceType.DESERT);
    }
}