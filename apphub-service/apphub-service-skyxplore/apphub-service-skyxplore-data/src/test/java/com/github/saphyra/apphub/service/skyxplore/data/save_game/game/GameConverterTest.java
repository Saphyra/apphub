package com.github.saphyra.apphub.service.skyxplore.data.save_game.game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
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
public class GameConverterTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID HOST = UUID.randomUUID();
    private static final String NAME = "name";
    private static final String GAME_ID_STRING = "game-id";
    private static final String HOST_STRING = "host";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private GameConverter underTest;

    @Test
    public void convertDomain() {
        GameModel model = new GameModel();
        model.setGameId(GAME_ID);
        model.setName(NAME);
        model.setHost(HOST);

        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(HOST)).willReturn(HOST_STRING);

        GameEntity result = underTest.convertDomain(model);

        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getName()).isEqualTo(NAME);
        assertThat(result.getHost()).isEqualTo(HOST_STRING);
    }

    @Test
    public void convertEntity() {
        GameEntity entity = GameEntity.builder()
            .gameId(GAME_ID_STRING)
            .host(HOST_STRING)
            .name(NAME)
            .build();

        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(HOST_STRING)).willReturn(HOST);

        GameModel result = underTest.convertEntity(entity);

        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getName()).isEqualTo(NAME);
        assertThat(result.getHost()).isEqualTo(HOST);
    }
}