package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.universe;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.UniverseModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class UniverseConverterTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final Integer SIZE = 23235;
    private static final String GAME_ID_STRING = "game-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private UniverseConverter underTest;

    @Test
    public void convertDomain() {
        UniverseModel model = new UniverseModel();
        model.setGameId(GAME_ID);
        model.setSize(SIZE);

        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);

        UniverseEntity result = underTest.convertDomain(model);

        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getSize()).isEqualTo(SIZE);
    }

    @Test
    public void convertEntity() {
        UniverseEntity entity = UniverseEntity.builder()
            .gameId(GAME_ID_STRING)
            .size(SIZE)
            .build();

        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);

        UniverseModel result = underTest.convertEntity(entity);

        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getProcessType()).isEqualTo(GameItemType.UNIVERSE);
        assertThat(result.getSize()).isEqualTo(SIZE);
    }
}