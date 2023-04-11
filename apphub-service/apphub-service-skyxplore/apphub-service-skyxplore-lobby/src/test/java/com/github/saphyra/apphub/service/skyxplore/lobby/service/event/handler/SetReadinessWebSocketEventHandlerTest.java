package com.github.saphyra.apphub.service.skyxplore.lobby.service.event.handler;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberStatus;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Member;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.member.LobbyMemberToResponseConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SetReadinessWebSocketEventHandlerTest {
    private static final UUID FROM = UUID.randomUUID();

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private MessageSenderProxy messageSenderProxy;

    @Mock
    private LobbyMemberToResponseConverter lobbyMemberToResponseConverter;

    @InjectMocks
    private SetReadinessWebSocketEventHandler underTest;

    @Mock
    private Lobby lobby;

    @Mock
    private Member member;

    @Mock
    private LobbyMemberResponse lobbyMemberResponse;

    @Test
    public void canHandle_setReadinessEvent() {
        assertThat(underTest.canHandle(WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS)).isTrue();
    }

    @Test
    public void canHandle_otherEvent() {
        assertThat(underTest.canHandle(WebSocketEventName.SKYXPLORE_GAME_USER_LEFT)).isFalse();
    }

    @Test
    public void setReadiness() {
        given(lobbyDao.findByUserIdValidated(FROM)).willReturn(lobby);
        Map<UUID, Member> lobbyMembers = CollectionUtils.singleValueMap(FROM, member);
        given(lobby.getMembers()).willReturn(lobbyMembers);
        given(lobbyMemberToResponseConverter.convertMember(member)).willReturn(lobbyMemberResponse);

        underTest.handle(FROM, WebSocketEvent.builder().payload(String.valueOf(true)).build());

        verify(member).setStatus(LobbyMemberStatus.READY);
        verify(messageSenderProxy).lobbyMemberModified(lobbyMemberResponse, lobbyMembers.keySet());
    }
}