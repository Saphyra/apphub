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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmptySurfaceProviderTest {
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final UUID SURFACE_WITH_BUILDING_ID = UUID.randomUUID();

    @Mock
    private MatchingSurfaceTypeFilter matchingSurfaceTypeFilter;

    @Mock
    private RandomEmptySurfaceProvider randomEmptySurfaceProvider;

    @Mock
    private AdjacentRandomEmptySurfaceProvider adjacentRandomEmptySurfaceProvider;

    @InjectMocks
    private EmptySurfaceProvider underTest;

    @Mock
    private Surface surface;

    @Mock
    private Surface surfaceWithBuilding;

    @Mock
    private Building building;

    @Mock
    private GameData gameData;

    @Mock
    private Buildings buildings;

    @Test
    public void randomEmptySurface() {
        given(matchingSurfaceTypeFilter.getSurfacesWithMatchingType(Arrays.asList(surface), SurfaceType.CONCRETE)).willReturn(Collections.emptyList());
        given(randomEmptySurfaceProvider.getRandomEmptySurface(Arrays.asList(surface), gameData)).willReturn(surface);

        Surface result = underTest.getEmptySurfaceForType(SurfaceType.CONCRETE, Arrays.asList(surface), gameData);

        assertThat(result).isEqualTo(surface);
        verify(surface).setSurfaceType(SurfaceType.CONCRETE);
    }

    @Test
    public void emptySurfaceWithMatchingType() {
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.findBySurfaceId(SURFACE_ID)).willReturn(Optional.empty());
        given(buildings.findBySurfaceId(SURFACE_WITH_BUILDING_ID)).willReturn(Optional.of(building));
        given(surface.getSurfaceId()).willReturn(SURFACE_ID);
        given(surfaceWithBuilding.getSurfaceId()).willReturn(SURFACE_WITH_BUILDING_ID);
        given(matchingSurfaceTypeFilter.getSurfacesWithMatchingType(Arrays.asList(surfaceWithBuilding, surface), SurfaceType.CONCRETE)).willReturn(Arrays.asList(surfaceWithBuilding, surface));

        Surface result = underTest.getEmptySurfaceForType(SurfaceType.CONCRETE, Arrays.asList(surfaceWithBuilding, surface), gameData);

        assertThat(result).isEqualTo(surface);
    }

    @Test
    public void randomEmptySurfaceNextToType() {
        given(matchingSurfaceTypeFilter.getSurfacesWithMatchingType(Arrays.asList(surfaceWithBuilding, surface), SurfaceType.CONCRETE)).willReturn(Arrays.asList(surfaceWithBuilding));
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.findBySurfaceId(SURFACE_WITH_BUILDING_ID)).willReturn(Optional.of(building));
        given(surfaceWithBuilding.getSurfaceId()).willReturn(SURFACE_WITH_BUILDING_ID);

        given(adjacentRandomEmptySurfaceProvider.getRandomEmptySurfaceNextTo(Arrays.asList(surfaceWithBuilding), Arrays.asList(surfaceWithBuilding, surface), gameData)).willReturn(surface);

        Surface result = underTest.getEmptySurfaceForType(SurfaceType.CONCRETE, Arrays.asList(surfaceWithBuilding, surface), gameData);

        assertThat(result).isEqualTo(surface);
        verify(surface).setSurfaceType(SurfaceType.CONCRETE);
    }
}