package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.coordinate;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CoordinateConverterTest {
    private static final UUID COORDINATE_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID REFERENCE_ID = UUID.randomUUID();
    private static final double X = 234;
    private static final double Y = 253467;
    private static final String COORDINATE_ID_STRING = "coordinate-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String REFERENCE_ID_STRING = "reference-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private CoordinateConverter underTest;

    @Test
    public void convertEntity() {
        CoordinateEntity entity = CoordinateEntity.builder()
            .coordinateId(COORDINATE_ID_STRING)
            .gameId(GAME_ID_STRING)
            .referenceId(REFERENCE_ID_STRING)
            .x(X)
            .y(Y)
            .build();

        given(uuidConverter.convertEntity(COORDINATE_ID_STRING)).willReturn(COORDINATE_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(REFERENCE_ID_STRING)).willReturn(REFERENCE_ID);

        CoordinateModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(COORDINATE_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.COORDINATE);
        assertThat(result.getReferenceId()).isEqualTo(REFERENCE_ID);
        assertThat(result.getCoordinate()).isEqualTo(new Coordinate(X, Y));
    }

    @Test
    public void convertDomain() {
        CoordinateModel model = new CoordinateModel();
        model.setId(COORDINATE_ID);
        model.setGameId(GAME_ID);
        model.setReferenceId(REFERENCE_ID);
        model.setCoordinate(new Coordinate(X, Y));

        given(uuidConverter.convertDomain(COORDINATE_ID)).willReturn(COORDINATE_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(REFERENCE_ID)).willReturn(REFERENCE_ID_STRING);

        CoordinateEntity result = underTest.convertDomain(model);

        assertThat(result.getCoordinateId()).isEqualTo(COORDINATE_ID_STRING);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getReferenceId()).isEqualTo(REFERENCE_ID_STRING);
        assertThat(result.getX()).isEqualTo(X);
        assertThat(result.getY()).isEqualTo(Y);
    }
}