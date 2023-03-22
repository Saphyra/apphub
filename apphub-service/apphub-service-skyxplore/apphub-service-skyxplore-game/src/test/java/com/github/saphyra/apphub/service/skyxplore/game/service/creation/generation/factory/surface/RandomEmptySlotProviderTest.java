package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.surface;

import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.surface.RandomEmptySlotProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class RandomEmptySlotProviderTest {
    @Mock
    private Random random;

    @InjectMocks
    private RandomEmptySlotProvider underTest;

    @Test
    public void getRandomEmptySlot() {
        given(random.randInt(0, 1)).willReturn(0)
            .willReturn(1);
        SurfaceType[][] surfaceMap = new SurfaceType[2][2];
        surfaceMap[0] = new SurfaceType[2];
        surfaceMap[1] = new SurfaceType[2];
        surfaceMap[0][1] = SurfaceType.DESERT;

        Optional<Coordinate> result = underTest.getRandomEmptySlot(surfaceMap);

        assertThat(result).contains(new Coordinate(1, 1));
    }

    @Test
    public void getRandomEmptySlotNextToSurfaceType_found() {
        SurfaceType[][] surfaceMap = new SurfaceType[2][2];
        surfaceMap[0] = new SurfaceType[2];
        surfaceMap[1] = new SurfaceType[2];
        surfaceMap[0][0] = SurfaceType.DESERT;
        surfaceMap[0][1] = SurfaceType.DESERT;

        given(random.randInt(0, 1)).willReturn(1);

        Optional<Coordinate> result = underTest.getRandomEmptySlotNextToSurfaceType(surfaceMap, SurfaceType.DESERT);

        assertThat(result).contains(new Coordinate(1, 1));
    }

    @Test
    public void getRandomEmptySlotNextToSurfaceType_notFound() {
        SurfaceType[][] surfaceMap = new SurfaceType[2][2];
        surfaceMap[0] = new SurfaceType[2];
        surfaceMap[1] = new SurfaceType[2];
        surfaceMap[0][0] = SurfaceType.DESERT;
        surfaceMap[0][1] = SurfaceType.CONCRETE;
        surfaceMap[1][0] = SurfaceType.CONCRETE;

        Optional<Coordinate> result = underTest.getRandomEmptySlotNextToSurfaceType(surfaceMap, SurfaceType.DESERT);

        assertThat(result).isEmpty();
    }

}