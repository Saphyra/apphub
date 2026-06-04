package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NotebookEventControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private DeleteByUserIdDao dao;

    private NotebookEventControllerImpl underTest;

    @BeforeEach
    public void setUp() {
        underTest = new NotebookEventControllerImpl(List.of(dao), accessTokenProvider);
    }

    @Test
    public void deleteAccountEvent() throws Exception {
        SendEventRequest<DeleteAccountEvent> eventRequest = SendEventRequest.<DeleteAccountEvent>builder().payload(new DeleteAccountEvent(USER_ID)).build();

        given(accessTokenProvider.set(AccessToken.builder().userId(USER_ID).build())).willReturn(accessTokenProvider);

        underTest.deleteAccountEvent(eventRequest);

        verify(accessTokenProvider).close();
        verify(dao).deleteByUserId(USER_ID);
    }
}