package com.github.saphyra.apphub.service.skyxplore.game.domain.data.player;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.alliance.Alliance;
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
    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USERNAME = "username";
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID ALLIANCE_ID = UUID.randomUUID();

    @InjectMocks
    private PlayerConverter underTest;

    @Mock
    private Game game;

    @Mock
    private Alliance alliance;

    @Test
    public void convert() {
        Player player = Player.builder()
            .playerId(PLAYER_ID)
            .userId(USER_ID)
            .playerName(USERNAME)
            .ai(true)
            .build();
        given(game.getGameId()).willReturn(GAME_ID);
        given(game.getAlliances()).willReturn(CollectionUtils.singleValueMap(UUID.randomUUID(), alliance));
        given(alliance.getMembers()).willReturn(CollectionUtils.singleValueMap(USER_ID, player));
        given(alliance.getAllianceId()).willReturn(ALLIANCE_ID);

        PlayerModel result = underTest.toModel(player, game);

        assertThat(result.getId()).isEqualTo(PLAYER_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.PLAYER);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getAi()).isTrue();
        assertThat(result.getAllianceId()).isEqualTo(ALLIANCE_ID);
    }
}