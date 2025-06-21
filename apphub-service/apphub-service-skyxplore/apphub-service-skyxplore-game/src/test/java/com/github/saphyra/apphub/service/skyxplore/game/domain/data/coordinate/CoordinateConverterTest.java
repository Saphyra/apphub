package com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CoordinateConverterTest {
    private static final UUID REFERRED_COORDINATE_ID = UUID.randomUUID();
    private static final UUID REFERENCE_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final Integer ORDER = 66;

    private final CoordinateConverter underTest = new CoordinateConverter();

    @Mock
    private Coordinate coordinate;

    @Test
    void convert() {
        ReferredCoordinate referredCoordinate = ReferredCoordinate.builder()
            .referredCoordinateId(REFERRED_COORDINATE_ID)
            .referenceId(REFERENCE_ID)
            .coordinate(coordinate)
            .order(ORDER)
            .build();

        CoordinateModel result = underTest.convert(GAME_ID, referredCoordinate);

        assertThat(result.getId()).isEqualTo(REFERRED_COORDINATE_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.COORDINATE);
        assertThat(result.getReferenceId()).isEqualTo(REFERENCE_ID);
        assertThat(result.getCoordinate()).isEqualTo(coordinate);
        assertThat(result.getOrder()).isEqualTo(ORDER);
    }
}