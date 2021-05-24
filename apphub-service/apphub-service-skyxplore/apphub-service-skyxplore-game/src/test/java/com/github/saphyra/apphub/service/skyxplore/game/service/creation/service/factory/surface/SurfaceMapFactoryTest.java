package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.surface;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SurfaceMapFactoryTest {
    private static final int PLANET_SIZE = 234;

    @Mock
    private EmptySurfaceMapFactory emptySurfaceMapFactory;

    @Mock
    private SurfaceMapFiller surfaceMapFiller;

    @InjectMocks
    private SurfaceMapFactory underTest;

    @Test
    public void createSurfaceMap() {
        SurfaceType[][] surfaceMap = new SurfaceType[1][0];

        given(emptySurfaceMapFactory.createEmptySurfaceMap(PLANET_SIZE)).willReturn(surfaceMap);

        SurfaceType[][] result = underTest.createSurfaceMap(PLANET_SIZE);

        verify(surfaceMapFiller).fillSurfaceMap(surfaceMap);

        assertThat(result).isEqualTo(surfaceMap);
    }
}