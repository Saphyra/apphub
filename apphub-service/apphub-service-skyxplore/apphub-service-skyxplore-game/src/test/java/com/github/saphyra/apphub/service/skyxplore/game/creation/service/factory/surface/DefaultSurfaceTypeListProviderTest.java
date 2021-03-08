package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.surface;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.creation.GameCreationProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSurfaceTypeListProviderTest {
    @Mock
    private GameCreationProperties properties;

    @Mock
    private GameCreationProperties.SurfaceProperties surfaceProperties;

    @Mock
    private GameCreationProperties.SurfaceTypeSpawnDetails spawnDetails;

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