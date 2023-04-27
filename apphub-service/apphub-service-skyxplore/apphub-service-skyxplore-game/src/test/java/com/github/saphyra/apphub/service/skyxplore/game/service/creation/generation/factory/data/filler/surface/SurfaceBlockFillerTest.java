package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.surface;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.surface.RandomEmptySlotProvider;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.surface.SurfaceBlockFiller;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SurfaceBlockFillerTest {
    @Mock
    private RandomEmptySlotProvider randomEmptySlotProvider;

    @InjectMocks
    private SurfaceBlockFiller underTest;

    @Test
    public void initialPlacement() {
        SurfaceType[][] surfaceMap = new SurfaceType[1][1];
        surfaceMap[0] = new SurfaceType[1];

        given(randomEmptySlotProvider.getRandomEmptySlot(surfaceMap)).willReturn(Optional.of(new Coordinate(0, 0)));

        underTest.fillBlockWithSurfaceType(surfaceMap, SurfaceType.DESERT, true);

        assertThat(surfaceMap[0][0]).isEqualTo(SurfaceType.DESERT);
    }

    @Test
    public void notInitialPlacement() {
        SurfaceType[][] surfaceMap = new SurfaceType[1][1];
        surfaceMap[0] = new SurfaceType[1];

        given(randomEmptySlotProvider.getRandomEmptySlotNextToSurfaceType(surfaceMap, SurfaceType.DESERT)).willReturn(Optional.of(new Coordinate(0, 0)));

        underTest.fillBlockWithSurfaceType(surfaceMap, SurfaceType.DESERT, false);

        assertThat(surfaceMap[0][0]).isEqualTo(SurfaceType.DESERT);
    }

}