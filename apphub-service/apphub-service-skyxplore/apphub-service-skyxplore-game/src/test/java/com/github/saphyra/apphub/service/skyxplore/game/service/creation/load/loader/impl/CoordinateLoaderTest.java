package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.Coordinates;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.ReferredCoordinate;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CoordinateLoaderTest {
    private static final UUID REFERRED_COORDINATE_ID = UUID.randomUUID();
    private static final UUID REFERENCE_ID = UUID.randomUUID();
    private static final Integer ORDER = 3214;

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private CoordinateLoader underTest;

    @Mock
    private Coordinates coordinates;

    @Mock
    private ReferredCoordinate referredCoordinate;

    @Mock
    private GameData gameData;

    @Mock
    private CoordinateModel coordinateModel;

    @Mock
    private Coordinate coordinate;

    @Test
    void getGameItemType() {
        assertThat(underTest.getGameItemType()).isEqualTo(GameItemType.COORDINATE);
    }

    @Test
    void getArrayClass() {
        assertThat(underTest.getArrayClass()).isEqualTo(CoordinateModel[].class);
    }

    @Test
    void addToGameData() {
        given(gameData.getCoordinates()).willReturn(coordinates);

        underTest.addToGameData(gameData, List.of(referredCoordinate));

        verify(coordinates).addAll(List.of(referredCoordinate));
    }

    @Test
    void convert() {
        given(coordinateModel.getId()).willReturn(REFERRED_COORDINATE_ID);
        given(coordinateModel.getReferenceId()).willReturn(REFERENCE_ID);
        given(coordinateModel.getCoordinate()).willReturn(coordinate);
        given(coordinateModel.getOrder()).willReturn(ORDER);

        ReferredCoordinate result = underTest.convert(coordinateModel);

        assertThat(result.getReferredCoordinateId()).isEqualTo(REFERRED_COORDINATE_ID);
        assertThat(result.getReferenceId()).isEqualTo(REFERENCE_ID);
        assertThat(result.getCoordinate()).isEqualTo(coordinate);
        assertThat(result.getOrder()).isEqualTo(ORDER);
    }
}