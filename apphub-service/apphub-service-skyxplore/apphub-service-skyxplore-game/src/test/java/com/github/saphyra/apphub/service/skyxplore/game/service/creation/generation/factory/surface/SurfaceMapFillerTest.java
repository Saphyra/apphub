package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.surface;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
public class SurfaceMapFillerTest {
    @Mock
    private SurfaceTypeListProvider surfaceTypeListProvider;

    @Mock
    private SurfaceBlockFiller surfaceBlockFiller;

    @InjectMocks
    private SurfaceMapFiller underTest;

    @Test
    public void fillSurfaceMap() {
        SurfaceType[][] surfaceMap = new SurfaceType[1][2];
        surfaceMap[0] = new SurfaceType[2];

        given(surfaceTypeListProvider.createSurfaceTypeList(true)).willReturn(Arrays.asList(SurfaceType.CONCRETE));
        given(surfaceTypeListProvider.createSurfaceTypeList(false)).willReturn(Arrays.asList(SurfaceType.DESERT));
        doAnswer(invocationOnMock -> surfaceMap[0][0] = SurfaceType.CONCRETE).when(surfaceBlockFiller).fillBlockWithSurfaceType(surfaceMap, SurfaceType.CONCRETE, true);
        doAnswer(invocationOnMock -> surfaceMap[0][1] = SurfaceType.DESERT).when(surfaceBlockFiller).fillBlockWithSurfaceType(surfaceMap, SurfaceType.DESERT, false);

        underTest.fillSurfaceMap(surfaceMap);

        assertThat(surfaceMap[0][0]).isEqualTo(SurfaceType.CONCRETE);
        assertThat(surfaceMap[0][1]).isEqualTo(SurfaceType.DESERT);
    }
}