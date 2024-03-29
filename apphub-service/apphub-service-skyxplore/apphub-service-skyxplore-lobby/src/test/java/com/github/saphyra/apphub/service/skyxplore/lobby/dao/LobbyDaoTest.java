package com.github.saphyra.apphub.service.skyxplore.lobby.dao;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyInvitationWebSocketHandler;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class LobbyDaoTest {
    private static final UUID LOBBY_ID_1 = UUID.randomUUID();
    private static final UUID LOBBY_ID_2 = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();
    private static final UUID CHARACTER_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private SkyXploreLobbyInvitationWebSocketHandler invitationWebSocketHandler;

    @InjectMocks
    private LobbyDao underTest;

    @Mock
    private Lobby lobby1;

    @Mock
    private Lobby lobby2;

    @Mock
    private LobbyPlayer lobbyPlayer;

    @BeforeEach
    public void setUp() {
        given(lobby1.getLobbyId()).willReturn(LOBBY_ID_1);
        given(lobby2.getLobbyId()).willReturn(LOBBY_ID_2);

        underTest.save(lobby1);
        underTest.save(lobby2);
    }

    @Test
    void findByHostValidated_notFound() {
        Throwable ex = catchThrowable(() -> underTest.findByHostValidated(USER_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.LOBBY_NOT_FOUND);
    }

    @Test
    void findByHostValidated_notHost() {
        given(lobby1.getPlayers()).willReturn(CollectionUtils.singleValueMap(USER_ID, lobbyPlayer));
        given(lobby1.getHost()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.findByHostValidated(USER_ID));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void findByHostValidated() {
        given(lobby1.getPlayers()).willReturn(CollectionUtils.singleValueMap(USER_ID, lobbyPlayer));
        given(lobby1.getHost()).willReturn(USER_ID);

        Lobby result = underTest.findByHostValidated(USER_ID);

        assertThat(result).isEqualTo(lobby1);
    }

    @Test
    public void getByLastAccessedBefore() {
        given(lobby1.getLastAccess()).willReturn(CURRENT_DATE.minusSeconds(1));
        given(lobby2.getLastAccess()).willReturn(CURRENT_DATE);

        List<Lobby> result = underTest.getByLastAccessedBefore(CURRENT_DATE);

        assertThat(result).containsExactly(lobby1);
    }

    @Test
    public void delete() {
        given(lobby1.getInvitations())
            .willReturn(CollectionUtils.toList(Invitation.builder().invitorId(USER_ID).characterId(CHARACTER_ID).build()));

        given(applicationContextProxy.getBean(SkyXploreLobbyInvitationWebSocketHandler.class)).willReturn(invitationWebSocketHandler);

        underTest.delete(lobby1);

        then(invitationWebSocketHandler).should().sendEvent(CHARACTER_ID, WebSocketEventName.SKYXPLORE_MAIN_MENU_CANCEL_INVITATION, USER_ID);
    }

    @Test
    void findByGameId() {
        given(lobby1.getGameId()).willReturn(GAME_ID);

        assertThat(underTest.findByGameId(GAME_ID)).contains(lobby1);
    }
}