package com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ReferredCoordinateFactoryTest {
    private static final UUID REFERRED_COORDINATE_ID = UUID.randomUUID();
    private static final UUID REFERENCE_ID = UUID.randomUUID();
    private static final Integer ORDER = 42;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private CoordinateConverter coordinateConverter;

    @InjectMocks
    private ReferredCoordinateFactory underTest;

    @Mock
    private Coordinate coordinate;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private GameData gameData;

    @Mock
    private Coordinates coordinates;

    @Mock
    private CoordinateModel model;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(REFERRED_COORDINATE_ID);

        ReferredCoordinate result = underTest.create(REFERENCE_ID, coordinate);

        assertThat(result.getReferredCoordinateId()).isEqualTo(REFERRED_COORDINATE_ID);
        assertThat(result.getReferenceId()).isEqualTo(REFERENCE_ID);
        assertThat(result.getCoordinate()).isEqualTo(coordinate);
    }

    @Test
    void save() {
        given(idGenerator.randomUuid()).willReturn(REFERRED_COORDINATE_ID);
        given(gameData.getCoordinates()).willReturn(coordinates);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(coordinateConverter.convert(eq(GAME_ID), any(ReferredCoordinate.class))).willReturn(model);

        ReferredCoordinate result = underTest.save(progressDiff, gameData, REFERENCE_ID, coordinate, ORDER);

        assertThat(result.getReferredCoordinateId()).isEqualTo(REFERRED_COORDINATE_ID);
        assertThat(result.getReferenceId()).isEqualTo(REFERENCE_ID);
        assertThat(result.getCoordinate()).isEqualTo(coordinate);
        assertThat(result.getOrder()).isEqualTo(ORDER);

        then(coordinates).should().add(result);
        then(progressDiff).should().save(model);
    }
}