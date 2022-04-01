package com.github.saphyra.apphub.service.skyxplore.game.common;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CoordinateModelFactoryTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID REFERENCE_ID = UUID.randomUUID();
    private static final UUID COORDINATE_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private CoordinateModelFactory underTest;

    @Mock
    private Coordinate coordinate;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(COORDINATE_ID);

        CoordinateModel result = underTest.create(coordinate, GAME_ID, REFERENCE_ID);

        assertThat(result.getId()).isEqualTo(COORDINATE_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getProcessType()).isEqualTo(GameItemType.COORDINATE);
        assertThat(result.getReferenceId()).isEqualTo(REFERENCE_ID);
        assertThat(result.getCoordinate()).isEqualTo(coordinate);
    }
}