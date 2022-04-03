package com.github.saphyra.apphub.service.skyxplore.game.common;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.LineModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class LineModelFactoryTest {
    private static final UUID LINE_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID REFERENCE_ID = UUID.randomUUID();
    private static final UUID A = UUID.randomUUID();
    private static final UUID B = UUID.randomUUID();

    @InjectMocks
    private LineModelFactory underTest;

    @Test
    public void create() {
        LineModel result = underTest.create(LINE_ID, GAME_ID, REFERENCE_ID, A, B);

        assertThat(result.getId()).isEqualTo(LINE_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.LINE);
        assertThat(result.getReferenceId()).isEqualTo(REFERENCE_ID);
        assertThat(result.getA()).isEqualTo(A);
        assertThat(result.getB()).isEqualTo(B);
    }
}