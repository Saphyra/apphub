package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PlayerLoaderTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final String USERNAME = "username";

    @Mock
    private GameItemLoader gameItemLoader;

    @Mock
    private GameDataProxy gameDataProxy;

    @InjectMocks
    private PlayerLoader underTest;

    @Mock
    private PlayerModel model;

    @Before
    public void setUp() {
        given(gameItemLoader.loadChildren(GAME_ID, GameItemType.PLAYER)).willReturn(Arrays.asList(model));

        given(model.getId()).willReturn(PLAYER_ID);
        given(model.getUserId()).willReturn(USER_ID);
        given(model.getAllianceId()).willReturn(ALLIANCE_ID);
        given(model.getUsername()).willReturn(USERNAME);
    }

    @Test
    public void loadAi() {
        given(model.getAi()).willReturn(true);

        Map<UUID, Player> result = underTest.load(GAME_ID, Collections.emptyList());

        assertThat(result).hasSize(1);
        Player player = result.get(USER_ID);
        assertThat(player.getPlayerId()).isEqualTo(PLAYER_ID);
        assertThat(player.getUserId()).isEqualTo(USER_ID);
        assertThat(player.getUsername()).isEqualTo(USERNAME);
        assertThat(player.getAllianceId()).isEqualTo(ALLIANCE_ID);
        assertThat(player.isAi()).isTrue();
        assertThat(player.isConnected()).isFalse();
    }

    @Test
    public void loadPlayer() {
        given(model.getAi()).willReturn(false);

        Map<UUID, Player> result = underTest.load(GAME_ID, Arrays.asList(USER_ID));

        assertThat(result).hasSize(1);
        Player player = result.get(USER_ID);
        assertThat(player.getPlayerId()).isEqualTo(PLAYER_ID);
        assertThat(player.getUserId()).isEqualTo(USER_ID);
        assertThat(player.getUsername()).isEqualTo(USERNAME);
        assertThat(player.getAllianceId()).isEqualTo(ALLIANCE_ID);
        assertThat(player.isAi()).isFalse();
        assertThat(player.isConnected()).isFalse();
    }

    @Test
    public void loadPlayer_playerTurnsToAi() {
        given(model.getAi()).willReturn(false);

        Map<UUID, Player> result = underTest.load(GAME_ID, Collections.emptyList());

        assertThat(result).hasSize(1);
        Player player = result.get(USER_ID);
        assertThat(player.getPlayerId()).isEqualTo(PLAYER_ID);
        assertThat(player.getUserId()).isEqualTo(USER_ID);
        assertThat(player.getUsername()).isEqualTo(USERNAME);
        assertThat(player.getAllianceId()).isEqualTo(ALLIANCE_ID);
        assertThat(player.isAi()).isTrue();
        assertThat(player.isConnected()).isFalse();

        verify(model).setAi(true);
        verify(gameDataProxy).saveListItem(model);
    }
}