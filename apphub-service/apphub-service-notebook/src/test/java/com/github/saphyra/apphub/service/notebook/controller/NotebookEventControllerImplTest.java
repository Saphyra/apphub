package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class NotebookEventControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private ContentDao contentDao;

    @InjectMocks
    private NotebookEventControllerImpl underTest;

    @Test
    public void deleteAccountEvent() {
        SendEventRequest<DeleteAccountEvent> eventRequest = SendEventRequest.<DeleteAccountEvent>builder().payload(new DeleteAccountEvent(USER_ID)).build();

        underTest.deleteAccountEvent(eventRequest);

        verify(listItemDao).deleteByUserId(USER_ID);
        verify(contentDao).deleteByUserId(USER_ID);
    }
}