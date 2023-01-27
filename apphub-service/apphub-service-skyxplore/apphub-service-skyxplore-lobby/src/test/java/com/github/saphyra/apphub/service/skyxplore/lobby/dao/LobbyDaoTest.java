package com.github.saphyra.apphub.service.skyxplore.lobby.dao;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LobbyDaoTest {
    private static final UUID LOBBY_ID_1 = UUID.randomUUID();
    private static final UUID LOBBY_ID_2 = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();
    private static final UUID CHARACTER_ID = UUID.randomUUID();

    @Mock
    private MessageSenderProxy messageSenderProxy;

    @InjectMocks
    private LobbyDao underTest;

    @Mock
    private Lobby lobby1;

    @Mock
    private Lobby lobby2;

    @Mock
    private Member member;

    @BeforeEach
    public void setUp() {
        given(lobby1.getLobbyId()).willReturn(LOBBY_ID_1);
        given(lobby2.getLobbyId()).willReturn(LOBBY_ID_2);

        underTest.save(lobby1);
        underTest.save(lobby2);
    }

    @Test
    public void findByUserId() {
        given(lobby1.getMembers()).willReturn(CollectionUtils.singleValueMap(USER_ID, member));

        Optional<Lobby> result = underTest.findByUserId(USER_ID);

        assertThat(result).contains(lobby1);
    }

    @Test
    public void findByUserIdValidated_notFound() {
        Throwable ex = catchThrowable(() -> underTest.findByUserIdValidated(USER_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.LOBBY_NOT_FOUND);
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

        underTest.delete(lobby1);

        assertThat(underTest.getAll()).containsExactly(lobby2);
        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToMainMenu(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getRecipients()).containsExactly(CHARACTER_ID);
        assertThat(argumentCaptor.getValue().getEvent().getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_MAIN_MENU_CANCEL_INVITATION);
        assertThat(argumentCaptor.getValue().getEvent().getPayload()).isEqualTo(USER_ID);
    }
}