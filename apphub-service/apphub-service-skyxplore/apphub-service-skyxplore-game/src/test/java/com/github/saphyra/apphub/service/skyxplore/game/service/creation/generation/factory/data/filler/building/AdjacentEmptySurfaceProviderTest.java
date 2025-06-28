package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.building;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionArea;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionAreas;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.Coordinates;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AdjacentEmptySurfaceProviderTest {
    private static final UUID SURFACE_ID = UUID.randomUUID();

    @Mock
    private DistanceCalculator distanceCalculator;

    @InjectMocks
    private AdjacentEmptySurfaceProvider underTest;

    @Mock
    private GameData gameData;

    @Mock
    private ConstructionAreas constructionAreas;

    @Mock
    private Coordinates coordinates;

    @Mock
    private Coordinate coordinate1;

    @Mock
    private Coordinate coordinate2;

    @Mock
    private Surface surface;

    @Mock
    private ConstructionArea constructionArea;

    @Test
    public void surfaceFound() {
        given(gameData.getCoordinates()).willReturn(coordinates);
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(constructionAreas.findBySurfaceId(SURFACE_ID)).willReturn(Optional.empty());
        given(surface.getSurfaceId()).willReturn(SURFACE_ID);
        given(coordinates.findByReferenceIdValidated(SURFACE_ID)).willReturn(coordinate2);
        given(distanceCalculator.getDistance(coordinate1, coordinate2)).willReturn(1d);

        Optional<Surface> result = underTest.getEmptySurfaceNextTo(coordinate1, Arrays.asList(surface), gameData);

        assertThat(result).contains(surface);
    }

    @Test
    public void hasBuilding() {
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(constructionAreas.findBySurfaceId(SURFACE_ID)).willReturn(Optional.of(constructionArea));
        given(surface.getSurfaceId()).willReturn(SURFACE_ID);

        Optional<Surface> result = underTest.getEmptySurfaceNextTo(coordinate1, Arrays.asList(surface), gameData);

        assertThat(result).isEmpty();
    }

    @Test
    public void surfaceNotFound() {
        given(gameData.getCoordinates()).willReturn(coordinates);
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(constructionAreas.findBySurfaceId(SURFACE_ID)).willReturn(Optional.empty());
        given(surface.getSurfaceId()).willReturn(SURFACE_ID);
        given(coordinates.findByReferenceIdValidated(SURFACE_ID)).willReturn(coordinate2);
        given(distanceCalculator.getDistance(coordinate1, coordinate2)).willReturn(2d);

        Optional<Surface> result = underTest.getEmptySurfaceNextTo(coordinate1, Arrays.asList(surface), gameData);

        assertThat(result).isEmpty();
    }
}