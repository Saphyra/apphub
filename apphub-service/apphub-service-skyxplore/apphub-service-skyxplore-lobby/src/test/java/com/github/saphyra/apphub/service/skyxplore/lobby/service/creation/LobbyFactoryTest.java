package com.github.saphyra.apphub.service.skyxplore.lobby.service.creation;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.api.skyxplore.model.game.AllianceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.AiPlayer;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberStatus;
import com.github.saphyra.apphub.api.skyxplore.response.game.GameViewForLobbyCreation;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyType;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Member;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LobbyFactoryTest {
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();
    private static final UUID LOBBY_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LOBBY_NAME = "lobby-name";
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final String ALLIANCE_NAME = "alliance-name";

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private GameSettingsProperties gameSettingsProperties;

    @InjectMocks
    private LobbyFactory underTest;

    @Mock
    private SkyXploreGameSettings settings;

    @Mock
    private GameViewForLobbyCreation gameViewForLobbyCreation;

    @Mock
    private AllianceModel allianceModel;

    @Mock
    private PlayerModel playerModel;

    @Mock
    private AiPlayer aiPlayer;

    @Test
    public void create() {
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_DATE);
        given(idGenerator.randomUuid()).willReturn(LOBBY_ID);
        given(gameSettingsProperties.createDefaultSettings()).willReturn(settings);

        Lobby result = underTest.createForNewGame(USER_ID, LOBBY_NAME);

        assertThat(result.getLobbyId()).isEqualTo(LOBBY_ID);
        assertThat(result.getGameId()).isNull();
        assertThat(result.getType()).isEqualTo(LobbyType.NEW_GAME);
        assertThat(result.getLobbyName()).isEqualTo(LOBBY_NAME);
        assertThat(result.getHost()).isEqualTo(USER_ID);
        assertThat(result.getMembers()).containsEntry(USER_ID, Member.builder().userId(USER_ID).status(LobbyMemberStatus.NOT_READY).build());
        assertThat(result.getLastAccess()).isEqualTo(CURRENT_DATE);
        assertThat(result.getSettings()).isEqualTo(settings);
    }

    @Test
    public void create_detailed() {
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_DATE);
        given(idGenerator.randomUuid()).willReturn(LOBBY_ID);
        given(gameViewForLobbyCreation.getHostAllianceId()).willReturn(ALLIANCE_ID);
        given(gameViewForLobbyCreation.getName()).willReturn(LOBBY_NAME);
        given(gameViewForLobbyCreation.getAlliances()).willReturn(List.of(allianceModel));
        given(gameViewForLobbyCreation.getPlayers()).willReturn(List.of(playerModel));
        given(gameViewForLobbyCreation.getAis()).willReturn(List.of(aiPlayer));
        given(allianceModel.getId()).willReturn(ALLIANCE_ID);
        given(allianceModel.getName()).willReturn(ALLIANCE_NAME);
        given(playerModel.getUserId()).willReturn(PLAYER_ID);

        Lobby result = underTest.createForLoadGame(USER_ID, GAME_ID, gameViewForLobbyCreation);

        assertThat(result.getLobbyId()).isEqualTo(LOBBY_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(LobbyType.LOAD_GAME);
        assertThat(result.getLobbyName()).isEqualTo(LOBBY_NAME);
        assertThat(result.getHost()).isEqualTo(USER_ID);
        assertThat(result.getMembers()).containsEntry(USER_ID, Member.builder().userId(USER_ID).alliance(ALLIANCE_ID).status(LobbyMemberStatus.NOT_READY).build());
        assertThat(result.getLastAccess()).isEqualTo(CURRENT_DATE);
        assertThat(result.getAlliances()).hasSize(1);
        assertThat(result.getAlliances().get(0).getAllianceId()).isEqualTo(ALLIANCE_ID);
        assertThat(result.getAlliances().get(0).getAllianceName()).isEqualTo(ALLIANCE_NAME);
        assertThat(result.getExpectedPlayers()).containsExactly(PLAYER_ID);
        assertThat(result.getAis()).containsExactly(aiPlayer);
    }
}