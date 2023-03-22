package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.surface;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.surface.EmptySurfaceMapFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.surface.SurfaceMapFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.surface.SurfaceMapFiller;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
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