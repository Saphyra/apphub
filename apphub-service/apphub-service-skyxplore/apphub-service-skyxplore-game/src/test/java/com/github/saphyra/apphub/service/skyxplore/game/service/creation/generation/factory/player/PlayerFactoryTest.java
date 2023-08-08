package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.player;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.AiPlayer;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.player.Player;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.CharacterProxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PlayerFactoryTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final String PLAYER_NAME = "player-name";
    private static final UUID PLAYER_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private CharacterProxy characterProxy;

    @InjectMocks
    private PlayerFactory underTest;

    @Mock
    private AiPlayer aiPlayer;

    @Test
    public void create() {
        given(characterProxy.getCharacterByUserId(USER_ID)).willReturn(SkyXploreCharacterModel.builder().name(PLAYER_NAME).build());
        given(idGenerator.randomUuid()).willReturn(PLAYER_ID);

        Map<UUID, Player> result = underTest.create(CollectionUtils.singleValueMap(USER_ID, ALLIANCE_ID));

        assertThat(result).containsKey(USER_ID);
        Player player = result.get(USER_ID);
        assertThat(player.getPlayerId()).isEqualTo(PLAYER_ID);
        assertThat(player.getUserId()).isEqualTo(USER_ID);
        assertThat(player.getPlayerName()).isEqualTo(PLAYER_NAME);
        assertThat(player.getAllianceId()).isEqualTo(ALLIANCE_ID);
        assertThat(player.getPlayerId()).isEqualTo(PLAYER_ID);
        assertThat(player.isAi()).isFalse();
        assertThat(player.isConnected()).isFalse();
    }

    @Test
    public void createAi() {
        given(aiPlayer.getUserId()).willReturn(USER_ID);
        given(idGenerator.randomUuid()).willReturn(PLAYER_ID);
        given(aiPlayer.getAllianceId()).willReturn(ALLIANCE_ID);
        given(aiPlayer.getName()).willReturn(PLAYER_NAME);

        Player result = underTest.createAi(aiPlayer);

        assertThat(result.getPlayerId()).isEqualTo(PLAYER_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getPlayerName()).isEqualTo(PLAYER_NAME);
        assertThat(result.getAllianceId()).isEqualTo(ALLIANCE_ID);
        assertThat(result.getPlayerId()).isEqualTo(PLAYER_ID);
        assertThat(result.isAi()).isTrue();
        assertThat(result.isConnected()).isTrue();
    }
}