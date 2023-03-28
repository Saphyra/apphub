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
    public Surface surface1;

    @Mock
    public Surface surface2;

    @Mock
    public Surface surface3;

    @Mock
    private Building building;

    @Mock
    private GameData gameData;

    @Mock
    private Buildings buildings;

    @Test
    public void getRandomEmptySurface() {
        given(surface1.getSurfaceType()).willReturn(SurfaceType.CONCRETE);
        given(surface2.getSurfaceType()).willReturn(SurfaceType.DESERT);
        given(surface3.getSurfaceType()).willReturn(SurfaceType.DESERT);

        given(gameData.getBuildings()).willReturn(buildings);
        given(surface1.getSurfaceId()).willReturn(SURFACE_ID);
        given(buildings.findBySurfaceId(SURFACE_ID)).willReturn(Optional.of(building));

        Surface result = underTest.getRandomEmptySurface(Arrays.asList(surface1, surface2, surface3), gameData);

        assertThat(result).isEqualTo(surface3);
    }

    @Test
    public void surfaceNotFound() {
        assertThat(catchThrowable(() -> underTest.getRandomEmptySurface(Collections.emptyList(), gameData))).isInstanceOf(IllegalStateException.class);
    }
}