package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.building;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class RandomEmptySurfaceProviderTest {
    private static final UUID SURFACE_ID = UUID.randomUUID();

    @InjectMocks
    private RandomEmptySurfaceProvider underTest;

    @Mock
    public Surface surfaceWithDifferentType;

    @Mock
    public Surface occupiedSurface;

    @Mock
    public Surface emptyDesert;

    @Mock
    private Building building;

    @Mock
    private GameData gameData;

    @Mock
    private Buildings buildings;

    @Test
    public void getRandomEmptySurface() {
        given(surfaceWithDifferentType.getSurfaceType()).willReturn(SurfaceType.CONCRETE);
        given(occupiedSurface.getSurfaceType()).willReturn(SurfaceType.DESERT);
        given(emptyDesert.getSurfaceType()).willReturn(SurfaceType.DESERT);

        given(gameData.getBuildings()).willReturn(buildings);
        given(occupiedSurface.getSurfaceId()).willReturn(SURFACE_ID);
        given(buildings.findBySurfaceId(SURFACE_ID)).willReturn(Optional.of(building));

        Surface result = underTest.getRandomEmptySurface(Arrays.asList(surfaceWithDifferentType, occupiedSurface, emptyDesert), gameData);

        assertThat(result).isEqualTo(emptyDesert);
    }

    @Test
    public void surfaceNotFound() {
        assertThat(catchThrowable(() -> underTest.getRandomEmptySurface(Collections.emptyList(), gameData))).isInstanceOf(IllegalStateException.class);
    }
}