package com.github.saphyra.apphub.service.skyxplore.lobby.service.creation;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Alliance;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyType;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Member;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class LobbyFactoryTest {
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();
    private static final UUID LOBBY_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LOBBY_NAME = "lobby-name";
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID PLAYER_ID = UUID.randomUUID();

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private LobbyFactory underTest;

    @Mock
    private Alliance alliance;

    @Test
    public void create() {
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        given(idGenerator.randomUuid()).willReturn(LOBBY_ID);

        Lobby result = underTest.create(USER_ID, LOBBY_NAME, LobbyType.LOAD_GAME);

        assertThat(result.getLobbyId()).isEqualTo(LOBBY_ID);
        assertThat(result.getGameId()).isNull();
        assertThat(result.getType()).isEqualTo(LobbyType.LOAD_GAME);
        assertThat(result.getLobbyName()).isEqualTo(LOBBY_NAME);
        assertThat(result.getHost()).isEqualTo(USER_ID);
        assertThat(result.getMembers()).containsEntry(USER_ID, Member.builder().userId(USER_ID).build());
        assertThat(result.getLastAccess()).isEqualTo(CURRENT_DATE);
    }

    @Test
    public void create_detailed() {
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        given(idGenerator.randomUuid()).willReturn(LOBBY_ID);

        Lobby result = underTest.create(USER_ID, GAME_ID, ALLIANCE_ID, LOBBY_NAME, LobbyType.LOAD_GAME, Arrays.asList(alliance), Arrays.asList(PLAYER_ID));

        assertThat(result.getLobbyId()).isEqualTo(LOBBY_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(LobbyType.LOAD_GAME);
        assertThat(result.getLobbyName()).isEqualTo(LOBBY_NAME);
        assertThat(result.getHost()).isEqualTo(USER_ID);
        assertThat(result.getMembers()).containsEntry(USER_ID, Member.builder().userId(USER_ID).alliance(ALLIANCE_ID).build());
        assertThat(result.getLastAccess()).isEqualTo(CURRENT_DATE);
        assertThat(result.getAlliances()).containsExactly(alliance);
        assertThat(result.getExpectedPlayers()).containsExactly(PLAYER_ID);
    }
}