package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class GameConverterTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID HOST = UUID.randomUUID();
    private static final String NAME = "name";
    private static final String GAME_ID_STRING = "game-id";
    private static final String HOST_STRING = "host";
    private static final LocalDateTime LAST_PLAYED = LocalDateTime.now();
    private static final LocalDateTime MARKED_FOR_DELETION_AT = LocalDateTime.now();
    private static final Integer UNIVERSE_SIZE = 324;

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
        model.setLastPlayed(LAST_PLAYED);
        model.setMarkedForDeletion(true);
        model.setMarkedForDeletionAt(MARKED_FOR_DELETION_AT);
        model.setUniverseSize(UNIVERSE_SIZE);

        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(HOST)).willReturn(HOST_STRING);

        GameEntity result = underTest.convertDomain(model);

        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getName()).isEqualTo(NAME);
        assertThat(result.getHost()).isEqualTo(HOST_STRING);
        assertThat(result.getLastPlayed()).isEqualTo(LAST_PLAYED);
        assertThat(result.getMarkedForDeletion()).isTrue();
        assertThat(result.getMarkedForDeletionAt()).isEqualTo(MARKED_FOR_DELETION_AT);
        assertThat(result.getUniverseSize()).isEqualTo(UNIVERSE_SIZE);
    }

    @Test
    public void convertEntity() {
        GameEntity entity = GameEntity.builder()
            .gameId(GAME_ID_STRING)
            .host(HOST_STRING)
            .name(NAME)
            .lastPlayed(LAST_PLAYED)
            .markedForDeletion(true)
            .markedForDeletionAt(MARKED_FOR_DELETION_AT)
            .universeSize(UNIVERSE_SIZE)
            .build();

        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(HOST_STRING)).willReturn(HOST);

        GameModel result = underTest.convertEntity(entity);

        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getName()).isEqualTo(NAME);
        assertThat(result.getHost()).isEqualTo(HOST);
        assertThat(result.getType()).isEqualTo(GameItemType.GAME);
        assertThat(result.getLastPlayed()).isEqualTo(LAST_PLAYED);
        assertThat(result.getMarkedForDeletion()).isTrue();
        assertThat(result.getMarkedForDeletionAt()).isEqualTo(MARKED_FOR_DELETION_AT);
        assertThat(result.getUniverseSize()).isEqualTo(UNIVERSE_SIZE);
    }
}