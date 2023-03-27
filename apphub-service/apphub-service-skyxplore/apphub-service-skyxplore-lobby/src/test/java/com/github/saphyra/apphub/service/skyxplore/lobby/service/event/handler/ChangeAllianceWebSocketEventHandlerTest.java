package com.github.saphyra.apphub.service.skyxplore.lobby.service.event.handler;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Alliance;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Member;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class ChangeAllianceWebSocketEventHandlerTest {
    private static final String NO_ALLIANCE = "no-alliance";
    private static final String NEW_ALLIANCE = "new-alliance";

    private static final UUID FROM = UUID.randomUUID();
    private static final Object PAYLOAD = "payload";
    private static final UUID DIFFERENT_MEMBER_ID = UUID.randomUUID();
    private static final UUID HOST = UUID.randomUUID();
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final String ALLIANCE_NAME = "alliance-name";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID NEW_ALLIANCE_ID = UUID.randomUUID();

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private MessageSenderProxy messageSenderProxy;

    @InjectMocks
    private ChangeAllianceWebSocketEventHandler underTest;

    @Mock
    private WebSocketEvent event;

    @Mock
    private ChangeAllianceWebSocketEventHandler.ChangeAllianceEvent changeAllianceEvent;

    @Mock
    private Lobby lobby;

    @Mock
    private Member member;

    @Mock
    private Alliance alliance;


    @Test
    public void canHandle_changeAllianceEvent() {
        assertThat(underTest.canHandle(WebSocketEventName.SKYXPLORE_LOBBY_CHANGE_ALLIANCE)).isTrue();
    }

    @Test
    public void canHandle_otherEvent() {
        assertThat(underTest.canHandle(WebSocketEventName.SKYXPLORE_GAME_USER_LEFT)).isFalse();
    }

    @Test
    public void allianceNotFoundForForbiddenOperation() {
        given(event.getPayload()).willReturn(PAYLOAD);
        given(objectMapperWrapper.convertValue(PAYLOAD, ChangeAllianceWebSocketEventHandler.ChangeAllianceEvent.class)).willReturn(changeAllianceEvent);
        given(lobbyDao.findByUserIdValidated(FROM)).willReturn(lobby);
        given(lobby.getHost()).willReturn(HOST);
        given(lobby.getAlliances()).willReturn(CollectionUtils.toList(alliance));
        given(lobby.getMembers()).willReturn(CollectionUtils.toMap(USER_ID, member));
        given(alliance.getAllianceId()).willReturn(ALLIANCE_ID);
        given(changeAllianceEvent.getUserId()).willReturn(DIFFERENT_MEMBER_ID);
        given(lobby.getMembers()).willReturn(CollectionUtils.toMap(DIFFERENT_MEMBER_ID, member));
        given(member.getAlliance()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.handle(FROM, event));

        ExceptionValidator.validateReportedException(ex, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERAL_ERROR);

        verifyNoInteractions(messageSenderProxy);
    }

    @Test
    public void forbiddenOperation_hasAlliance() {
        given(event.getPayload()).willReturn(PAYLOAD);
        given(objectMapperWrapper.convertValue(PAYLOAD, ChangeAllianceWebSocketEventHandler.ChangeAllianceEvent.class)).willReturn(changeAllianceEvent);
        given(lobbyDao.findByUserIdValidated(FROM)).willReturn(lobby);
        given(lobby.getHost()).willReturn(HOST);
        given(lobby.getAlliances()).willReturn(CollectionUtils.toList(alliance));
        given(lobby.getMembers()).willReturn(CollectionUtils.toMap(USER_ID, member));
        given(alliance.getAllianceId()).willReturn(ALLIANCE_ID);
        given(alliance.getAllianceName()).willReturn(ALLIANCE_NAME);
        given(changeAllianceEvent.getUserId()).willReturn(DIFFERENT_MEMBER_ID);
        given(lobby.getMembers()).willReturn(CollectionUtils.toMap(DIFFERENT_MEMBER_ID, member));
        given(member.getAlliance()).willReturn(ALLIANCE_ID);

        Throwable ex = catchThrowable(() -> underTest.handle(FROM, event));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION);
        verifyMessageSent(DIFFERENT_MEMBER_ID, ALLIANCE_NAME, false, lobby.getMembers());
    }

    @Test
    public void forbiddenOperation_noAlliance() {
        given(event.getPayload()).willReturn(PAYLOAD);
        given(objectMapperWrapper.convertValue(PAYLOAD, ChangeAllianceWebSocketEventHandler.ChangeAllianceEvent.class)).willReturn(changeAllianceEvent);
        given(lobbyDao.findByUserIdValidated(FROM)).willReturn(lobby);
        given(lobby.getHost()).willReturn(HOST);
        given(lobby.getMembers()).willReturn(CollectionUtils.toMap(USER_ID, member));
        given(changeAllianceEvent.getUserId()).willReturn(DIFFERENT_MEMBER_ID);
        given(lobby.getMembers()).willReturn(CollectionUtils.toMap(DIFFERENT_MEMBER_ID, member));
        given(member.getAlliance()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.handle(FROM, event));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION);

        verifyMessageSent(DIFFERENT_MEMBER_ID, NO_ALLIANCE, false, lobby.getMembers());
    }

    @Test
    public void noAlliance() {
        given(event.getPayload()).willReturn(PAYLOAD);
        given(objectMapperWrapper.convertValue(PAYLOAD, ChangeAllianceWebSocketEventHandler.ChangeAllianceEvent.class)).willReturn(changeAllianceEvent);
        given(lobbyDao.findByUserIdValidated(FROM)).willReturn(lobby);
        given(lobby.getHost()).willReturn(HOST);
        given(lobby.getMembers()).willReturn(CollectionUtils.toMap(USER_ID, member));
        given(changeAllianceEvent.getUserId()).willReturn(FROM);
        given(lobby.getMembers()).willReturn(CollectionUtils.toMap(FROM, member));
        given(changeAllianceEvent.getAlliance()).willReturn(NO_ALLIANCE);

        underTest.handle(FROM, event);

        verify(member).setAlliance(null);

        verifyMessageSent(FROM, NO_ALLIANCE, false, lobby.getMembers());
    }

    @Test
    public void newAlliance() {
        given(event.getPayload()).willReturn(PAYLOAD);
        given(objectMapperWrapper.convertValue(PAYLOAD, ChangeAllianceWebSocketEventHandler.ChangeAllianceEvent.class)).willReturn(changeAllianceEvent);
        given(lobbyDao.findByUserIdValidated(FROM)).willReturn(lobby);
        given(lobby.getHost()).willReturn(HOST);
        given(lobby.getAlliances()).willReturn(CollectionUtils.toList(alliance));
        given(lobby.getMembers()).willReturn(CollectionUtils.toMap(USER_ID, member));
        given(changeAllianceEvent.getUserId()).willReturn(FROM);
        given(lobby.getMembers()).willReturn(CollectionUtils.toMap(FROM, member));
        given(changeAllianceEvent.getAlliance()).willReturn(NEW_ALLIANCE);
        given(idGenerator.randomUuid()).willReturn(NEW_ALLIANCE_ID);

        underTest.handle(FROM, event);

        assertThat(lobby.getAlliances()).containsExactlyInAnyOrder(alliance, Alliance.builder().allianceId(NEW_ALLIANCE_ID).allianceName("2").build());
        verify(member).setAlliance(NEW_ALLIANCE_ID);

        verifyMessageSent(FROM, "2", true, lobby.getMembers());
    }

    @Test
    public void switchAlliance_notFound() {
        given(event.getPayload()).willReturn(PAYLOAD);
        given(objectMapperWrapper.convertValue(PAYLOAD, ChangeAllianceWebSocketEventHandler.ChangeAllianceEvent.class)).willReturn(changeAllianceEvent);
        given(lobbyDao.findByUserIdValidated(FROM)).willReturn(lobby);
        given(lobby.getHost()).willReturn(HOST);
        given(lobby.getAlliances()).willReturn(CollectionUtils.toList(alliance));
        given(lobby.getMembers()).willReturn(CollectionUtils.toMap(USER_ID, member));
        given(alliance.getAllianceName()).willReturn(ALLIANCE_NAME);
        given(changeAllianceEvent.getUserId()).willReturn(FROM);
        given(lobby.getMembers()).willReturn(CollectionUtils.toMap(FROM, member));
        given(changeAllianceEvent.getAlliance()).willReturn("2");

        Throwable ex = catchThrowable(() -> underTest.handle(FROM, event));

        ExceptionValidator.validateReportedException(ex, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERAL_ERROR);
    }

    @Test
    public void switchAlliance() {
        given(event.getPayload()).willReturn(PAYLOAD);
        given(objectMapperWrapper.convertValue(PAYLOAD, ChangeAllianceWebSocketEventHandler.ChangeAllianceEvent.class)).willReturn(changeAllianceEvent);
        given(lobbyDao.findByUserIdValidated(FROM)).willReturn(lobby);
        given(lobby.getHost()).willReturn(HOST);
        given(lobby.getAlliances()).willReturn(CollectionUtils.toList(alliance));
        given(lobby.getMembers()).willReturn(CollectionUtils.toMap(USER_ID, member));
        given(alliance.getAllianceId()).willReturn(ALLIANCE_ID);
        given(alliance.getAllianceName()).willReturn(ALLIANCE_NAME);
        given(changeAllianceEvent.getUserId()).willReturn(FROM);
        given(lobby.getMembers()).willReturn(CollectionUtils.toMap(FROM, member));
        given(changeAllianceEvent.getAlliance()).willReturn(ALLIANCE_NAME);

        underTest.handle(FROM, event);

        verify(member).setAlliance(ALLIANCE_ID);

        verifyMessageSent(FROM, ALLIANCE_NAME, false, lobby.getMembers());
    }

    private void verifyMessageSent(UUID userId, String allianceName, boolean newAlliance, Map<UUID, Member> members) {
        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToLobby(argumentCaptor.capture());

        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getRecipients()).isEqualTo(new ArrayList<>(members.keySet()));

        WebSocketEvent event = message.getEvent();
        assertThat(event.getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_LOBBY_CHANGE_ALLIANCE);

        ChangeAllianceWebSocketEventHandler.AllianceChangedEvent payload = (ChangeAllianceWebSocketEventHandler.AllianceChangedEvent) event.getPayload();
        assertThat(payload.getUserId()).isEqualTo(userId);
        assertThat(payload.getAlliance()).isEqualTo(allianceName);
        assertThat(payload.isNewAlliance()).isEqualTo(newAlliance);
    }
}