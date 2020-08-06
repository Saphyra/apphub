package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class NotebookEventControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private DeleteByUserIdDao dao;

    private NotebookEventControllerImpl underTest;

    @Before
    public void setUp(){
        underTest = new NotebookEventControllerImpl(Arrays.asList(dao));
    }

    @Test
    public void deleteAccountEvent() {
        SendEventRequest<DeleteAccountEvent> eventRequest = SendEventRequest.<DeleteAccountEvent>builder().payload(new DeleteAccountEvent(USER_ID)).build();

        underTest.deleteAccountEvent(eventRequest);

        verify(dao).deleteByUserId(USER_ID);
    }
}