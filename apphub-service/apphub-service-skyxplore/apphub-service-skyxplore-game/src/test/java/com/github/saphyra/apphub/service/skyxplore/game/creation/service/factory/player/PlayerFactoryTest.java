package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.player;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.creation.service.RandomNameProvider;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.CharacterProxy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class PlayerFactoryTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PLAYER_NAME = "player-name";
    private static final UUID PLAYER_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private RandomNameProvider randomNameProvider;

    @Mock
    private CharacterProxy characterProxy;

    @InjectMocks
    private PlayerFactory underTest;

    @Mock
    private SkyXploreCharacterModel characterModel;

    @Test
    public void create_ai() {
        List<String> usedPlayerNames = new ArrayList<>();
        given(randomNameProvider.getRandomName(usedPlayerNames)).willReturn(PLAYER_NAME);
        given(idGenerator.randomUuid()).willReturn(PLAYER_ID);

        Player result = underTest.create(USER_ID, true, usedPlayerNames);

        assertThat(result.getPlayerId()).isEqualTo(PLAYER_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getUsername()).isEqualTo(PLAYER_NAME);
        assertThat(result.isAi()).isTrue();
        assertThat(result.isConnected()).isTrue();
        assertThat(usedPlayerNames).containsExactly(PLAYER_NAME);
    }

    @Test
    public void create_realPlayer() {
        List<String> usedPlayerNames = new ArrayList<>();
        given(characterProxy.getCharacterByUserId(USER_ID)).willReturn(characterModel);
        given(characterModel.getName()).willReturn(PLAYER_NAME);
        given(idGenerator.randomUuid()).willReturn(PLAYER_ID);

        Player result = underTest.create(USER_ID, false, usedPlayerNames);

        assertThat(result.getPlayerId()).isEqualTo(PLAYER_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getUsername()).isEqualTo(PLAYER_NAME);
        assertThat(result.isAi()).isFalse();
        assertThat(result.isConnected()).isFalse();

        assertThat(usedPlayerNames).containsExactly(PLAYER_NAME);
    }
}