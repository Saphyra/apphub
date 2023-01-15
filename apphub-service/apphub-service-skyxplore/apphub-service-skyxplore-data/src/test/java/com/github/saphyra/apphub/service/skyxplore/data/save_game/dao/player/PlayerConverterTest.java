package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.player;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PlayerConverterTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final String USERNAME = "username";
    private static final String USER_ID_STRING = "user-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String ALLIANCE_ID_STRING = "alliance";
    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final String PLAYER_ID_STRING = "player-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private PlayerConverter underTest;

    @Test
    public void convertDomain() {
        PlayerModel model = new PlayerModel();
        model.setId(PLAYER_ID);
        model.setGameId(GAME_ID);
        model.setAllianceId(ALLIANCE_ID);
        model.setUserId(USER_ID);
        model.setUsername(USERNAME);
        model.setAi(true);

        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(PLAYER_ID)).willReturn(PLAYER_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(ALLIANCE_ID)).willReturn(ALLIANCE_ID_STRING);

        PlayerEntity result = underTest.convertDomain(model);

        assertThat(result.getPlayerId()).isEqualTo(PLAYER_ID_STRING);
        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getAllianceId()).isEqualTo(ALLIANCE_ID_STRING);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.isAi()).isTrue();
    }

    @Test
    public void convertEntity() {
        PlayerEntity entity = PlayerEntity.builder()
            .playerId(PLAYER_ID_STRING)
            .userId(USER_ID_STRING)
            .gameId(GAME_ID_STRING)
            .allianceId(ALLIANCE_ID_STRING)
            .username(USERNAME)
            .ai(true)
            .build();

        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(PLAYER_ID_STRING)).willReturn(PLAYER_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(ALLIANCE_ID_STRING)).willReturn(ALLIANCE_ID);

        PlayerModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(PLAYER_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.PLAYER);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getAllianceId()).isEqualTo(ALLIANCE_ID);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getAi()).isTrue();
    }
}