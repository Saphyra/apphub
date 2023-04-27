package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.surface;

import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.SurfaceProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.SurfaceTypeSpawnDetails;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.surface.DefaultSurfaceTypeListProvider;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.surface.SurfaceTypeListProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SurfaceTypeListProviderTest {
    @Mock
    private Random random;

    @Mock
    private GameProperties properties;

    @Mock
    private DefaultSurfaceTypeListProvider defaultSurfaceTypeListProvider;

    @InjectMocks
    private SurfaceTypeListProvider underTest;

    @Mock
    private SurfaceProperties surfaceProperties;

    @Mock
    private SurfaceTypeSpawnDetails spawnDetails;

    @Test
    public void createSurfaceTypeList_initial_remove() {
        given(defaultSurfaceTypeListProvider.getSurfaceTypes()).willReturn(new ArrayList<>(Arrays.asList(SurfaceType.ORE_FIELD)));
        given(random.randBoolean()).willReturn(true);

        given(properties.getSurface()).willReturn(surfaceProperties);
        given(surfaceProperties.getSpawnDetails()).willReturn(Arrays.asList(spawnDetails));
        given(spawnDetails.isOptional()).willReturn(true);
        given(spawnDetails.getSurfaceName()).willReturn(SurfaceType.ORE_FIELD.name());

        List<SurfaceType> result = underTest.createSurfaceTypeList(true);

        assertThat(result).isEmpty();
    }

    @Test
    public void createSurfaceTypeList_initial_keep() {
        given(defaultSurfaceTypeListProvider.getSurfaceTypes()).willReturn(new ArrayList<>(Arrays.asList(SurfaceType.ORE_FIELD)));

        given(properties.getSurface()).willReturn(surfaceProperties);
        given(surfaceProperties.getSpawnDetails()).willReturn(Arrays.asList(spawnDetails));
        given(spawnDetails.isOptional()).willReturn(false);

        List<SurfaceType> result = underTest.createSurfaceTypeList(true);

        assertThat(result).containsExactly(SurfaceType.ORE_FIELD);
    }

    @Test
    public void createSurfaceTypeList_initial_notOptional() {
        given(defaultSurfaceTypeListProvider.getSurfaceTypes()).willReturn(new ArrayList<>(Arrays.asList(SurfaceType.ORE_FIELD)));

        given(properties.getSurface()).willReturn(surfaceProperties);
        given(surfaceProperties.getSpawnDetails()).willReturn(Arrays.asList(spawnDetails));
        given(spawnDetails.isOptional()).willReturn(false);

        List<SurfaceType> result = underTest.createSurfaceTypeList(true);

        assertThat(result).containsExactly(SurfaceType.ORE_FIELD);
    }

    @Test
    public void createSurfaceTypeList_notInitial() {
        given(defaultSurfaceTypeListProvider.getSurfaceTypes()).willReturn(new ArrayList<>(Arrays.asList(SurfaceType.ORE_FIELD)));

        List<SurfaceType> result = underTest.createSurfaceTypeList(false);

        assertThat(result).containsExactly(SurfaceType.ORE_FIELD);
    }
}